package com.voyager.core

import android.content.Context
import android.net.Uri
import android.util.Xml
import android.view.View
import com.voyager.core.cache.LayoutCache
import com.voyager.core.data.utils.FileHelper.getFileExtension
import com.voyager.core.exceptions.VoyagerParsingException.XmlParsingException
import com.voyager.core.exceptions.VoyagerRenderingException.ViewInflationException
import com.voyager.core.model.ConfigManager
import com.voyager.core.model.ViewNode
import com.voyager.core.renderer.XmlRenderer
import com.voyager.core.threading.DispatcherProvider
import com.voyager.core.utils.ContextUtils.name
import com.voyager.core.utils.logging.LoggerFactory
import com.voyager.core.view.processor.BaseViewAttributes
import com.voyager.core.view.utils.ViewExtensions.getGeneratedViewInfo
import kotlinx.coroutines.rx3.rxSingle
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.util.zip.CRC32
import java.util.zip.CheckedInputStream

/**
 * Core class for the Voyager XML runtime engine.
 * Handles XML parsing, rendering, and resource management with optimized performance.
 */
class Voyager internal constructor(
    private val context: Context,
    theme: Int,
    private val layoutCache: LayoutCache,
    private val dispatcherProvider: DispatcherProvider,
) {

    init {
        BaseViewAttributes.initializeAttributes()
    }

    /**
     * Renderer responsible for converting parsed XML data into actual Android views.
     */
    private val renderer by lazy { XmlRenderer(context, theme) }

    private val isLoggingEnabled by lazy { ConfigManager.config.isLoggingEnabled }

    /**
     * Parses XML content from a given Uri.
     *
     * This function performs the following steps:
     * 1. Validates that the file extension is "xml".
     * 2. Attempts to retrieve the parsed layout from the `layoutCache` using the file's SHA256 hash as the key.
     *    If found, it returns the cached result.
     * 3. If not found in the cache, it opens an `InputStream` for the `xmlFile`.
     * 4. Parses the XML from the `InputStream` using `FileHelper.parseXML`.
     * 5. Converts the parsed XML (which is expected to be in a specific JSON format) into a `ViewNode` structure
     *    using `ViewNodeParser.fromJson`.
     * 6. Stores the successfully parsed `ViewNode` into the `layoutCache` with its SHA256 hash.
     * 7. Returns a `Result` object containing the parsed layout (`ViewNode`) on success, or an `Exception` on failure.
     *
     * This function is executed on an I/O dispatcher provided by `dispatcherProvider`.
     *
     * @param xmlFile The [Uri] of the XML file to parse.
     * @return A [Result] instance. If parsing is successful, it contains the parsed layout object (expected to be a `ViewNode`).
     *         If parsing fails, it contains an [Exception] detailing the error.
     *         Possible failure reasons include:
     *         - Unsupported file type (not "xml").
     *         - Failure to open an `InputStream` for the given `xmlFile`.
     *         - `FileHelper.parseXML` returns null (indicating XML parsing failure).
     *         - `ViewNodeParser.fromJson` returns null (indicating failure to convert parsed XML to `ViewNode`).
     *         - Any other `Exception` occurring during the process.
     */
    suspend fun parseXml(xmlFile: Uri) = withContext(dispatcherProvider.io) {
        Result.runCatching {
            val extension = getFileExtension(context, xmlFile)
            if (!extension.equals(
                    "xml", ignoreCase = true
                )
            ) throw XmlParsingException("Unsupported file type: $extension")

            context.contentResolver.openInputStream(xmlFile)?.use { inputStream ->
                val result = parsing(inputStream).getOrThrow()

                if (result.hash == null || result.rootNode == null) throw XmlParsingException(
                    "Failed to parse XML from URI: $xmlFile"
                )

                val node = layoutCache.getOrPut(result.hash) { result.rootNode }
                if (isLoggingEnabled) {
                    LoggerFactory.getLogger().debug("parseXml", "Parsed Xml file for URI: $xmlFile")
                }

                node
            } ?: throw XmlParsingException("Failed to open input stream for URI: $xmlFile")
        }

//        try {
//            val extension = getFileExtension(context, xmlFile)
//            if (extension != "xml") return@withContext Result.failure(Exception("Unsupported file type: $extension"))
//
//            context.contentResolver.openInputStream(xmlFile)?.use { inputStream ->
//                val tokenStream = ViewNodeTokenStream()
//                FileHelper.parseXML(inputStream, tokenStream)
//
//                val parseResult = tokenStream.getResult()
//                if (parseResult == null) return@withContext Result.failure(Exception("Failed to parse XML from URI: $xmlFile"))
//
//                // Check cache using the hash from ParseResult
//                layoutCache.get(parseResult.sha256Hash.contentHashCode())?.let {
//                    return@withContext Result.success(it)
//                }
//
//                val finalResult = parseResult.jsonString
//                finalResult.activityName = context.name
//                LoggerFactory.getLogger("Voyager")
//                    .error("parseXml", "Parsed Json for String: $finalResult")
//
//                layoutCache.put(parseResult.sha256Hash.contentHashCode(), finalResult)
//                return@withContext Result.success(finalResult)
//            }
//            Result.failure(Exception("Could not open InputStream for XML URI: $xmlFile"))
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
    }

    private fun parsing(inputStream: InputStream) = Result.runCatching {
        val crc = CRC32()
        val checkedInputStream = CheckedInputStream(inputStream, crc)

        checkedInputStream.use { stream ->
            val parser = Xml.newPullParser().apply {
                setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                setInput(stream, "UTF-8")
            }

            var rootNode: ViewNode? = null
            val stack = ArrayDeque<ViewNode>()

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val newNode =
                            ViewNode(parser.name, context.name, Xml.asAttributeSet(parser))
                        stack.lastOrNull()?.children?.add(newNode)
                        rootNode = rootNode ?: newNode
                        stack.add(newNode)
                    }

                    XmlPullParser.END_TAG -> {
                        if (stack.isNotEmpty()) stack.removeLast()
                    }
                }
                eventType = parser.next()
            }
            val hashBytes = crc.value.toInt()
            return@runCatching ParsedXmlResult(
                rootNode ?: throw XmlParsingException("XML parsing resulted in a null root node."),
                hashBytes
            )
        }
    }

    /**
     * Parses XML content with optimized performance, designed for RxJava integration.
     * @param xmlFile The XML Uri to parse
     * @return Single emitting the parsed layout or error
     */
    fun parseXmlRx(xmlFile: Uri) = rxSingle { parseXml(xmlFile).getOrThrow() }

    /**
     * Renders XML content or a pre-parsed [ViewNode] into a view hierarchy.
     *
     * This function can take either an XML file [Uri] or a [ViewNode] as input.
     * If an [xmlFile] is provided, it will first be parsed using [parseXml].
     * If a [node] is provided, it will be used directly for rendering.
     * If neither is provided, the function will return a failure.
     *
     * The rendering process is handled by the internal [XmlRenderer].
     *
     * This function is executed on an I/O dispatcher provided by `dispatcherProvider`.
     *
     * @param xmlFile The [Uri] of the XML file to render. Optional if `node` is provided.
     * @param node A pre-parsed [ViewNode] to render. Optional if `xmlFile` is provided.
     * @return A [Result] instance.
     *         On success, it contains the rendered view object (typically an Android `View` or `ViewGroup`).
     *         On failure, it contains an [Exception] detailing the error.
     *         Possible failure reasons include:
     *         - Both `xmlFile` and `node` are null.
     *         - Failure to parse the `xmlFile` (if provided).
     *         - Any `Exception` occurring during the rendering process.
     */
    suspend fun render(
        xmlFile: Uri? = null,
        node: ViewNode? = null,
    ) = withContext(dispatcherProvider.io) {
        Result.runCatching {
            if (xmlFile == null && node == null) throw ViewInflationException("No XML or ViewNode provided")

            val parsedLayout = if (xmlFile != null) parseXml(xmlFile).getOrThrow() else node
            if (parsedLayout == null) throw XmlParsingException("Failed to parse XML")

            // Render the parsed layout
            val result = renderer.render(parsedLayout)

            result
        }
    }

    /**
     * Renders XML content or a pre-parsed [ViewNode] into a view hierarchy with reactive programming support (RxJava).
     *
     * This function wraps the [render] method to provide a `Single` for RxJava integration.
     * It allows rendering from either an XML file [Uri] or a pre-parsed [ViewNode].
     *
     * @param xmlFile The [Uri] of the XML file to render. Optional if `node` is provided.
     * @param node A pre-parsed [ViewNode] to render. Optional if `xmlFile` is provided.
     * @return A `Single` that emits the rendered view (`View` or `ViewGroup`) upon successful rendering,
     *         or an error if rendering fails (e.g., parsing error, invalid input).
     */
    fun renderRx(xmlFile: Uri? = null, node: ViewNode? = null) =
        rxSingle { render(xmlFile, node).getOrThrow() }

    /**
     * Clears the layout cache to free memory.
     */
    suspend fun clearCache() = withContext(dispatcherProvider.io) {
        layoutCache.clear()
    }

    fun clearCacheRx() = rxSingle { clearCache() }

    fun setDelegate(view: View?, delegate: Any) {
        view?.getGeneratedViewInfo()?.delegate = delegate
    }
}


/**
 * A data class to hold the result of the parsing operation.
 * @param rootNode The root of the parsed ViewNode tree.
 * @param hash The SHA-256 hash of the input stream.
 */
data class ParsedXmlResult(val rootNode: ViewNode?, val hash: Int?)