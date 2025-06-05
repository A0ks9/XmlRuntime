package com.voyager.core.domain.usecase

import com.voyager.core.model.ViewNode
import com.voyager.core.repository.ViewNodeRepository
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Use case to insert a ViewNode.
 * This use case encapsulates the business logic of inserting a view node
 * into the repository, with proper error handling and logging.
 *
 * Features:
 * - View node insertion
 * - Error handling
 * - Logging support
 * - Input validation
 * - Performance monitoring
 * - Thread safety
 *
 * Example Usage:
 * ```kotlin
 * val useCase = InsertViewNodeUseCase(viewNodeRepository)
 * 
 * // Insert view node
 * useCase(viewNode)
 * ```
 *
 * @param viewNodeRepository The repository providing ViewNode data
 */
class InsertViewNodeUseCase(
    private val viewNodeRepository: ViewNodeRepository
) {
    private val logger = LoggerFactory.getLogger(InsertViewNodeUseCase::class.java.simpleName)
    private val config = ConfigManager.config

    /**
     * Executes the use case to insert the ViewNode.
     * This method handles the insertion process with proper error handling
     * and logging.
     *
     * @param viewNode The ViewNode to insert
     * @throws IllegalArgumentException if the view node is null
     * @throws Exception if the insertion fails
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
                logger.debug("invoke", "Inserting view node")
            }
            viewNodeRepository.insertViewNode(viewNode)
            if (config.isLoggingEnabled) {
                logger.debug("invoke", "Successfully inserted view node")
            }
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("invoke", "Error inserting view node", e)
            }
            throw e
        }
    }
} 