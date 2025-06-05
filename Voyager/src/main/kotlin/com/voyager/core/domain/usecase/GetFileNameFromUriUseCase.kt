package com.voyager.core.domain.usecase

import android.content.ContentResolver
import android.net.Uri
import com.voyager.core.repository.XmlRepository
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Use case to get the file name from a Content Uri.
 * This use case encapsulates the business logic of retrieving file names
 * from URIs, with proper error handling and logging.
 *
 * Features:
 * - File name resolution
 * - Error handling
 * - Logging support
 * - Input validation
 * - Performance monitoring
 *
 * Example Usage:
 * ```kotlin
 * val useCase = GetFileNameFromUriUseCase(xmlRepository)
 * 
 * // Get file name from URI
 * useCase(contentResolver, uri) { fileName ->
 *     // Handle file name
 *     println("File name: $fileName")
 * }
 * ```
 *
 * @param xmlRepository The repository providing XML operations
 */
class GetFileNameFromUriUseCase(
    private val xmlRepository: XmlRepository
) {
    private val logger = LoggerFactory.getLogger(GetFileNameFromUriUseCase::class.java.simpleName)
    private val config = ConfigManager.config

    /**
     * Executes the use case to get the file name.
     * This method handles the file name retrieval process with proper
     * error handling and logging.
     *
     * @param contentResolver The ContentResolver to use
     * @param uri The Uri of the file
     * @param callback A function to receive the file name
     * @throws IllegalArgumentException if any parameter is null
     */
    operator fun invoke(
        contentResolver: ContentResolver,
        uri: Uri,
        callback: (String) -> Unit
    ) {
        if (contentResolver == null) {
            if (config.isLoggingEnabled) {
                logger.error("invoke", "Content resolver is null")
            }
            throw IllegalArgumentException("Content resolver cannot be null")
        }

        if (uri == null) {
            if (config.isLoggingEnabled) {
                logger.error("invoke", "URI is null")
            }
            throw IllegalArgumentException("URI cannot be null")
        }

        if (callback == null) {
            if (config.isLoggingEnabled) {
                logger.error("invoke", "Callback is null")
            }
            throw IllegalArgumentException("Callback cannot be null")
        }

        try {
            if (config.isLoggingEnabled) {
                logger.debug("invoke", "Retrieving file name for URI: $uri")
            }
            xmlRepository.getFileNameFromUri(contentResolver, uri) { fileName ->
                if (config.isLoggingEnabled) {
                    logger.debug("invoke", "Retrieved file name: $fileName")
                }
                callback(fileName)
            }
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("invoke", "Error retrieving file name", e)
            }
            callback("unknown_file")
        }
    }
} 