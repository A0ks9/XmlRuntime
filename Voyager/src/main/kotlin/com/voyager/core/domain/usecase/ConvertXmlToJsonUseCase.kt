package com.voyager.core.domain.usecase

import com.voyager.core.repository.XmlRepository
import java.io.InputStream

/**
 * Use case to convert an XML InputStream to a JSON string.
 *
 * @param xmlRepository The repository providing XML operations.
 */
class ConvertXmlToJsonUseCase(
    private val xmlRepository: XmlRepository
) {
    /**
     * Executes the use case to convert the XML InputStream.
     *
     * @param inputStream The InputStream containing the XML.
     * @return The JSON string representation or null if conversion fails.
     */
    operator fun invoke(inputStream: InputStream): String? {
        return xmlRepository.convertXmlToJson(inputStream)
    }
} 