package com.voyager.core.exceptions

/**
 * Base exception class for all Voyager-related exceptions.
 * This allows catching all Voyager exceptions with a single catch block.
 *
 * Features:
 * - Hierarchical exception structure
 * - Detailed error messages
 * - Cause tracking
 * - Exception categorization
 *
 * Example Usage:
 * ```kotlin
 * try {
 *     // Voyager operation
 * } catch (e: VoyagerException) {
 *     when (e) {
 *         is VoyagerParsingException -> // Handle parsing error
 *         is VoyagerRenderingException -> // Handle rendering error
 *         is VoyagerResourceException -> // Handle resource error
 *         is VoyagerCacheException -> // Handle cache error
 *         is VoyagerNativeException -> // Handle native error
 *         is VoyagerConfigException -> // Handle config error
 *         is VoyagerDatabaseException -> // Handle database error
 *         is VoyagerNetworkException -> // Handle network error
 *     }
 * }
 * ```
 */
sealed class VoyagerException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Thrown when there are issues with XML parsing or conversion.
 * This exception hierarchy covers all XML and JSON related errors.
 */
sealed class VoyagerParsingException(message: String, cause: Throwable? = null) : VoyagerException(message, cause) {
    /**
     * Thrown when the XML file cannot be parsed.
     * This includes syntax errors, encoding issues, and malformed XML.
     */
    class XmlParsingException(message: String, cause: Throwable? = null) : VoyagerParsingException(message, cause)

    /**
     * Thrown when the JSON conversion fails.
     * This includes invalid JSON structure and type conversion errors.
     */
    class JsonConversionException(message: String, cause: Throwable? = null) : VoyagerParsingException(message, cause)

    /**
     * Thrown when the XML file is malformed or invalid.
     * This includes structural errors and validation failures.
     */
    class InvalidXmlException(message: String, cause: Throwable? = null) : VoyagerParsingException(message, cause)

    /**
     * Thrown when the XML schema validation fails.
     * This includes missing required elements and invalid attribute values.
     */
    class SchemaValidationException(message: String, cause: Throwable? = null) : VoyagerParsingException(message, cause)
}

/**
 * Thrown when there are issues with view rendering.
 * This exception hierarchy covers all view-related errors.
 */
sealed class VoyagerRenderingException(message: String, cause: Throwable? = null) : VoyagerException(message, cause) {
    /**
     * Thrown when a required attribute is missing.
     * This includes missing mandatory attributes for view types.
     */
    class MissingAttributeException(attributeName: String, viewType: String) : 
        VoyagerRenderingException("Missing required attribute '$attributeName' for view type '$viewType'")

    /**
     * Thrown when an attribute value is invalid.
     * This includes type mismatches and out-of-range values.
     */
    class InvalidAttributeValueException(attributeName: String, value: String, viewType: String) :
        VoyagerRenderingException("Invalid value '$value' for attribute '$attributeName' in view type '$viewType'")

    /**
     * Thrown when a view type is not supported.
     * This includes custom views that aren't registered.
     */
    class UnsupportedViewTypeException(viewType: String) :
        VoyagerRenderingException("Unsupported view type: '$viewType'")

    /**
     * Thrown when view inflation fails.
     * This includes layout inflation errors and resource resolution failures.
     */
    class ViewInflationException(message: String, cause: Throwable? = null) : VoyagerRenderingException(message, cause)
}

/**
 * Thrown when there are issues with resource handling.
 * This exception hierarchy covers all resource-related errors.
 */
sealed class VoyagerResourceException(message: String, cause: Throwable? = null) : VoyagerException(message, cause) {
    /**
     * Thrown when a resource cannot be found.
     * This includes missing resources and invalid resource IDs.
     */
    class ResourceNotFoundException(resourceType: String, resourceName: String) :
        VoyagerResourceException("Resource not found: $resourceType/$resourceName")

    /**
     * Thrown when a resource type is not supported.
     * This includes custom resource types that aren't registered.
     */
    class UnsupportedResourceTypeException(resourceType: String) :
        VoyagerResourceException("Unsupported resource type: '$resourceType'")

    /**
     * Thrown when resource loading fails.
     * This includes file system errors and resource corruption.
     */
    class ResourceLoadException(message: String, cause: Throwable? = null) : VoyagerResourceException(message, cause)
}

/**
 * Thrown when there are issues with the layout cache.
 * This exception hierarchy covers all caching-related errors.
 */
sealed class VoyagerCacheException(message: String, cause: Throwable? = null) : VoyagerException(message, cause) {
    /**
     * Thrown when the cache operation fails.
     * This includes read/write errors and permission issues.
     */
    class CacheOperationException(message: String, cause: Throwable? = null) : VoyagerCacheException(message, cause)

    /**
     * Thrown when the cache is corrupted.
     * This includes data corruption and invalid cache entries.
     */
    class CacheCorruptionException(message: String, cause: Throwable? = null) : VoyagerCacheException(message, cause)

    /**
     * Thrown when the cache is full.
     * This includes memory limits and storage capacity issues.
     */
    class CacheFullException(message: String, cause: Throwable? = null) : VoyagerCacheException(message, cause)
}

/**
 * Thrown when there are issues with native library operations.
 * This exception hierarchy covers all native code-related errors.
 */
sealed class VoyagerNativeException(message: String, cause: Throwable? = null) : VoyagerException(message, cause) {
    /**
     * Thrown when the native library fails to load.
     * This includes missing libraries and version mismatches.
     */
    class NativeLibraryLoadException(libraryName: String, cause: Throwable? = null) :
        VoyagerNativeException("Failed to load native library: '$libraryName'", cause)

    /**
     * Thrown when a native operation fails.
     * This includes JNI errors and native code crashes.
     */
    class NativeOperationException(message: String, cause: Throwable? = null) :
        VoyagerNativeException(message, cause)

    /**
     * Thrown when native memory allocation fails.
     * This includes out-of-memory conditions and memory leaks.
     */
    class NativeMemoryException(message: String, cause: Throwable? = null) :
        VoyagerNativeException(message, cause)
}

/**
 * Thrown when there are issues with configuration.
 * This exception hierarchy covers all configuration-related errors.
 */
sealed class VoyagerConfigException(message: String, cause: Throwable? = null) : VoyagerException(message, cause) {
    /**
     * Thrown when a required configuration is missing.
     * This includes missing mandatory settings.
     */
    class MissingConfigException(configName: String) :
        VoyagerConfigException("Missing required configuration: '$configName'")

    /**
     * Thrown when a configuration value is invalid.
     * This includes type mismatches and out-of-range values.
     */
    class InvalidConfigValueException(configName: String, value: String) :
        VoyagerConfigException("Invalid value '$value' for configuration '$configName'")

    /**
     * Thrown when configuration loading fails.
     * This includes file system errors and parsing failures.
     */
    class ConfigLoadException(message: String, cause: Throwable? = null) :
        VoyagerConfigException(message, cause)
}

/**
 * Thrown when there are issues with database operations.
 * This exception hierarchy covers all database-related errors.
 */
sealed class VoyagerDatabaseException(message: String, cause: Throwable? = null) : VoyagerException(message, cause) {
    /**
     * Thrown when a database operation fails.
     * This includes SQL errors and transaction failures.
     */
    class DatabaseOperationException(message: String, cause: Throwable? = null) :
        VoyagerDatabaseException(message, cause)

    /**
     * Thrown when a database query fails.
     * This includes syntax errors and constraint violations.
     */
    class DatabaseQueryException(message: String, cause: Throwable? = null) :
        VoyagerDatabaseException(message, cause)

    /**
     * Thrown when database migration fails.
     * This includes schema changes and data migration errors.
     */
    class DatabaseMigrationException(message: String, cause: Throwable? = null) :
        VoyagerDatabaseException(message, cause)
}

/**
 * Thrown when there are issues with network operations.
 * This exception hierarchy covers all network-related errors.
 */
sealed class VoyagerNetworkException(message: String, cause: Throwable? = null) : VoyagerException(message, cause) {
    /**
     * Thrown when a network request fails.
     * This includes connection errors and timeouts.
     */
    class NetworkRequestException(message: String, cause: Throwable? = null) :
        VoyagerNetworkException(message, cause)

    /**
     * Thrown when network authentication fails.
     * This includes invalid credentials and token errors.
     */
    class NetworkAuthException(message: String, cause: Throwable? = null) :
        VoyagerNetworkException(message, cause)

    /**
     * Thrown when network response parsing fails.
     * This includes invalid response formats and parsing errors.
     */
    class NetworkResponseException(message: String, cause: Throwable? = null) :
        VoyagerNetworkException(message, cause)
} 