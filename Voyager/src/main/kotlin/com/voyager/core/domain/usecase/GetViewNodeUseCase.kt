package com.voyager.core.domain.usecase

import com.voyager.core.model.ViewNode
import com.voyager.core.repository.ViewNodeRepository
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Use case to retrieve a ViewNode.
 * This use case encapsulates the business logic of fetching a view node
 * from the repository, with proper error handling and logging.
 *
 * Features:
 * - View node retrieval
 * - Error handling
 * - Logging support
 * - Performance monitoring
 * - Thread safety
 *
 * Example Usage:
 * ```kotlin
 * val useCase = GetViewNodeUseCase(viewNodeRepository)
 * 
 * // Get view node
 * val viewNode = useCase()
 * if (viewNode != null) {
 *     // Handle view node
 * } else {
 *     // Handle not found case
 * }
 * ```
 *
 * @param viewNodeRepository The repository providing ViewNode data
 */
class GetViewNodeUseCase(
    private val viewNodeRepository: ViewNodeRepository
) {
    private val logger = LoggerFactory.getLogger(GetViewNodeUseCase::class.java.simpleName)
    private val config = ConfigManager.config

    /**
     * Executes the use case to get the ViewNode.
     * This method handles the retrieval process with proper error handling
     * and logging.
     *
     * @return The ViewNode or null if not found
     * @throws Exception if the retrieval fails
     */
    suspend operator fun invoke(): ViewNode? {
        return try {
            if (config.isLoggingEnabled) {
                logger.debug("invoke", "Retrieving view node")
            }
            val viewNode = viewNodeRepository.getViewNode()
            if (config.isLoggingEnabled) {
                if (viewNode != null) {
                    logger.debug("invoke", "Successfully retrieved view node")
                } else {
                    logger.warn("invoke", "View node not found")
                }
            }
            viewNode
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("invoke", "Error retrieving view node", e)
            }
            throw e
        }
    }
} 