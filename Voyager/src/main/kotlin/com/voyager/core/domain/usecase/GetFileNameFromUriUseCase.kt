package com.voyager.core.domain.usecase

import android.content.ContentResolver
import android.net.Uri
import com.voyager.core.repository.XmlRepository

/**
 * Use case to get the file name from a Content Uri.
 *
 * @param xmlRepository The repository providing XML operations.
 */
class GetFileNameFromUriUseCase(
    private val xmlRepository: XmlRepository
) {
    /**
     * Executes the use case to get the file name.
     *
     * @param contentResolver The ContentResolver to use.
     * @param uri The Uri of the file.
     * @param callback A function to receive the file name.
     */
    operator fun invoke(contentResolver: ContentResolver, uri: Uri, callback: (String) -> Unit) {
        xmlRepository.getFileNameFromUri(contentResolver, uri, callback)
    }
} 