package com.voyager.core.domain.usecase

import com.voyager.core.model.ViewNode
import com.voyager.core.repository.ViewNodeRepository

/**
 * Use case to insert a ViewNode.
 *
 * This use case encapsulates the business logic of inserting a view node
 * into the repository.
 *
 * @param viewNodeRepository The repository providing ViewNode data.
 */
class InsertViewNodeUseCase(
    private val viewNodeRepository: ViewNodeRepository
) {
    /**
     * Executes the use case to insert the ViewNode.
     *
     * @param viewNode The ViewNode to insert.
     */
    suspend operator fun invoke(viewNode: ViewNode) {
        viewNodeRepository.insertViewNode(viewNode)
    }
} 