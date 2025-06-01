package com.voyager.core.domain.usecase

import com.voyager.core.repository.ViewNodeRepository

/**
 * Use case to check if a ViewNode exists.
 *
 * @param viewNodeRepository The repository providing ViewNode data.
 */
class HasViewNodeUseCase(
    private val viewNodeRepository: ViewNodeRepository
) {
    /**
     * Executes the use case to check for ViewNode existence.
     *
     * @return `true` if a ViewNode exists, `false` otherwise.
     */
    suspend operator fun invoke(): Boolean {
        return viewNodeRepository.hasViewNode()
    }
} 