package com.voyager.core.domain.usecase

import com.voyager.core.model.ViewNode
import com.voyager.core.repository.ViewNodeRepository

/**
 * Use case to retrieve a ViewNode.
 *
 * This use case encapsulates the business logic of fetching a view node
 * from the repository.
 *
 * @param viewNodeRepository The repository providing ViewNode data.
 */
class GetViewNodeUseCase(
    private val viewNodeRepository: ViewNodeRepository
) {
    /**
     * Executes the use case to get the ViewNode.
     *
     * @return The ViewNode or null if not found.
     */
    suspend operator fun invoke(): ViewNode? {
        return viewNodeRepository.getViewNode()
    }
} 