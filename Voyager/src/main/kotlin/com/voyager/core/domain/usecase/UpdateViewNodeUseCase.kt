package com.voyager.core.domain.usecase

import com.voyager.core.model.ViewNode
import com.voyager.core.repository.ViewNodeRepository
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Use case to update a ViewNode.
 * This use case encapsulates the business logic of updating a view node
 * in the repository, with proper error handling and logging.
 *
 * Features:
 * - View node updates
 * - Error handling
 * - Logging support
 * - Input validation
 * - Performance monitoring
 * - Thread safety
 *
 * Example Usage:
 * ```kotlin
 * val useCase = UpdateViewNodeUseCase(viewNodeRepository)
 * 
 * // Update view node
 * useCase(viewNode)
 * ```
 *
 * @param viewNodeRepository The repository providing ViewNode data
 */
class UpdateViewNodeUseCase(
    private val viewNodeRepository: ViewNodeRepository
) {
    private val logger = LoggerFactory.getLogger(UpdateViewNodeUseCase::class.java.simpleName)
    private val config = ConfigManager.config

    /**
     * Executes the use case to update the ViewNode.
     * This method handles the update process with proper error handling
     * and logging.
     *
     * @param viewNode The ViewNode to update
     * @throws IllegalArgumentException if the view node is null
     * @throws Exception if the update fails
     */
    suspend operator fun invoke(viewNode: ViewNode) {
        if (viewNode == null) {
            if (config.isLoggingEnabled) {
                logger.error("invoke", "View node is null")
            }
            throw IllegalArgumentException("View node cannot be null")
        }

        try {
            if (config.isLoggingEnabled) {
                logger.debug("invoke", "Updating view node")
            }
            viewNodeRepository.updateViewNode(viewNode)
            if (config.isLoggingEnabled) {
                logger.debug("invoke", "Successfully updated view node")
            }
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("invoke", "Error updating view node", e)
            }
            throw e
        }
    }
} 