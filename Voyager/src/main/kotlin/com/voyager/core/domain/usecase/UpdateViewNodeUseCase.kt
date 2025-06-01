package com.voyager.core.domain.usecase

import com.voyager.core.model.ViewNode
import com.voyager.core.repository.ViewNodeRepository

/**
 * Use case to update a ViewNode.
 *
 * This use case encapsulates the business logic of updating a view node
 * in the repository.
 *
 * @param viewNodeRepository The repository providing ViewNode data.
 */
class UpdateViewNodeUseCase(
    private val viewNodeRepository: ViewNodeRepository
) {
    /**
     * Executes the use case to update the ViewNode.
     *
     * @param viewNode The ViewNode to update.
     */
    suspend operator fun invoke(viewNode: ViewNode) {
        viewNodeRepository.updateViewNode(viewNode)
    }
} 