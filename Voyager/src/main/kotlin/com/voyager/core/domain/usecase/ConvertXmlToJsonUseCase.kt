package com.voyager.core.domain.usecase

import com.voyager.core.repository.XmlRepository
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory
import java.io.InputStream

/**
 * Use case to convert an XML InputStream to a JSON string.
 * This use case encapsulates the business logic of converting XML data
 * to JSON format, with proper error handling and logging.
 *
 * Features:
 * - XML to JSON conversion
 * - Error handling
 * - Logging support
 * - Input validation
 * - Performance monitoring
 *
 * Example Usage:
 * ```kotlin
 * val useCase = ConvertXmlToJsonUseCase(xmlRepository)
 * 
 * // Convert XML to JSON
 * val json = useCase(inputStream)
 * if (json != null) {
 *     // Handle successful conversion
 * } else {
 *     // Handle conversion failure
 * }
 * ```
 *
 * @param xmlRepository The repository providing XML operations
 */
class ConvertXmlToJsonUseCase(
    private val xmlRepository: XmlRepository
) {
    private val logger = LoggerFactory.getLogger(ConvertXmlToJsonUseCase::class.java.simpleName)
    private val config = ConfigManager.config

    /**
     * Executes the use case to convert the XML InputStream.
     * This method handles the conversion process with proper error handling
     * and logging.
     *
     * @param inputStream The InputStream containing the XML
     * @return The JSON string representation or null if conversion fails
     * @throws IllegalArgumentException if the input stream is null
     */
    operator fun invoke(inputStream: InputStream): String? {
        if (inputStream == null) {
            if (config.isLoggingEnabled) {
                logger.error("invoke", "Input stream is null")
            }
            throw IllegalArgumentException("Input stream cannot be null")
        }

        return try {
            if (config.isLoggingEnabled) {
                logger.debug("invoke", "Starting XML to JSON conversion")
            }
            val result = xmlRepository.convertXmlToJson(inputStream)
            if (config.isLoggingEnabled) {
                if (result != null) {
                    logger.debug("invoke", "Successfully converted XML to JSON")
                } else {
                    logger.warn("invoke", "XML conversion returned null")
                }
            }
            result
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("invoke", "Error converting XML to JSON", e)
            }
            null
        }
    }
} 