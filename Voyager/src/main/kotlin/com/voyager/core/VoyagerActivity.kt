package com.voyager.core

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.voyager.core.model.ViewNode
import com.voyager.core.utils.logging.LoggerFactory
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * Base activity class for Voyager integration.
 * Handles Voyager lifecycle and provides convenient access to Voyager instance.
 *
 * Not Finished
 */
internal abstract class VoyagerActivity : AppCompatActivity() {
    private val logger = LoggerFactory.getLogger(VoyagerActivity::class.java.simpleName)

    /**
     * The Voyager instance for this activity.
     * This is initialized lazily when first accessed.
     */
    protected val voyager: Voyager by inject { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logger.debug("onCreate", "Voyager initialized for activity: ${javaClass.simpleName}")
    }

    override fun onDestroy() {


        logger.debug("onDestroy", "Voyager detached from activity: ${javaClass.simpleName}")

        super.onDestroy()
    }

    /**
     * Convenience method to render XML content.
     * This method uses the activity's context for rendering.
     */
    protected suspend fun renderXml(xmlFile: Uri) = voyager.render(xmlFile = xmlFile)

    /**
     * Convenience method to render a pre-parsed ViewNode.
     * This method uses the activity's context for rendering.
     */
    protected suspend fun renderNode(node: ViewNode) = voyager.render(node = node)
} 