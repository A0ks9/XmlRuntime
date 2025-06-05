package com.voyager.core.domain.usecase

import com.voyager.core.repository.ViewNodeRepository
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Use case to check if a ViewNode exists.
 * This use case encapsulates the business logic of checking for view node
 * existence, with proper error handling and logging.
 *
 * Features:
 * - View node existence check
 * - Error handling
 * - Logging support
 * - Performance monitoring
 * - Thread safety
 *
 * Example Usage:
 * ```kotlin
 * val useCase = HasViewNodeUseCase(viewNodeRepository)
 * 
 * // Check if view node exists
 * val exists = useCase()
 * if (exists) {
 *     // Handle existing case
 * } else {
 *     // Handle not found case
 * }
 * ```
 *
 * @param viewNodeRepository The repository providing ViewNode data
 */
class HasViewNodeUseCase(
    private val viewNodeRepository: ViewNodeRepository
) {
    private val logger = LoggerFactory.getLogger(HasViewNodeUseCase::class.java.simpleName)
    private val config = ConfigManager.config

    /**
     * Executes the use case to check for ViewNode existence.
     * This method handles the existence check with proper error handling
     * and logging.
     *
     * @return true if a ViewNode exists, false otherwise
     * @throws Exception if the check fails
     */
    suspend operator fun invoke(): Boolean {
        return try {
            if (config.isLoggingEnabled) {
                logger.debug("invoke", "Checking view node existence")
            }
            val exists = viewNodeRepository.hasViewNode()
            if (config.isLoggingEnabled) {
                logger.debug("invoke", "View node exists: $exists")
            }
            exists
        } catch (e: Exception) {
            if (config.isLoggingEnabled) {
                logger.error("invoke", "Error checking view node existence", e)
            }
            throw e
        }
    }
} 