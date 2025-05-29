package com.voyager.data.models

import com.voyager.utils.interfaces.ResourcesProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class VoyagerConfigTest {

    @Mock
    lateinit var mockResourcesProvider: ResourcesProvider

    // Default values from VoyagerConfig.kt
    private val defaultVersion = "1.0.0-Beta01"
    private val defaultIsLoggingEnabled = false

    @Test
    fun `instantiation with all parameters sets properties correctly`() {
        val testVersion = "1.1.0"
        val testIsLoggingEnabled = true

        val config = VoyagerConfig(
            version = testVersion,
            isLoggingEnabled = testIsLoggingEnabled,
            provider = mockResourcesProvider
        )

        assertEquals(testVersion, config.version)
        assertEquals(testIsLoggingEnabled, config.isLoggingEnabled)
        assertEquals(mockResourcesProvider, config.provider)
    }

    @Test
    fun `instantiation with default parameters uses default values`() {
        val config = VoyagerConfig(provider = mockResourcesProvider)

        assertEquals(defaultVersion, config.version)
        assertEquals(defaultIsLoggingEnabled, config.isLoggingEnabled)
        assertEquals(mockResourcesProvider, config.provider)
    }

    @Test
    fun `data class equals and hashCode work as expected`() {
        val config1 = VoyagerConfig(
            version = "1.0",
            isLoggingEnabled = true,
            provider = mockResourcesProvider
        )
        val config2 = VoyagerConfig(
            version = "1.0",
            isLoggingEnabled = true,
            provider = mockResourcesProvider
        )
        val config3 = VoyagerConfig(
            version = "1.1", // Different version
            isLoggingEnabled = true,
            provider = mockResourcesProvider
        )
        val config4 = VoyagerConfig(
            version = "1.0",
            isLoggingEnabled = false, // Different logging status
            provider = mockResourcesProvider
        )

        // Create another mock provider to test inequality based on provider instance if needed,
        // though for data classes, if it's the same instance, it's equal.
        val anotherMockResourcesProvider: ResourcesProvider = mock(ResourcesProvider::class.java)
        val config5 = VoyagerConfig(
            version = "1.0",
            isLoggingEnabled = true,
            provider = anotherMockResourcesProvider // Different provider instance
        )


        assertEquals("Instances with same parameters should be equal", config1, config2)
        assertEquals("HashCodes for equal instances should be same", config1.hashCode(), config2.hashCode())

        assertNotEquals("Instances with different versions should not be equal", config1, config3)
        assertNotEquals("Instances with different logging status should not be equal", config1, config4)
        assertNotEquals("Instances with different provider instances should not be equal", config1, config5)
    }

    @Test
    fun `data class toString contains property names and values`() {
        val config = VoyagerConfig(
            version = "test-v1",
            isLoggingEnabled = true,
            provider = mockResourcesProvider
        )
        val configString = config.toString()

        assertTrue("toString should contain version property", configString.contains("version=test-v1"))
        assertTrue("toString should contain isLoggingEnabled property", configString.contains("isLoggingEnabled=true"))
        assertTrue("toString should contain provider property", configString.contains("provider=$mockResourcesProvider"))
    }

    @Test
    fun `copy method creates new instance with modified and copied properties`() {
        val originalConfig = VoyagerConfig(
            version = "original-1.0",
            isLoggingEnabled = false,
            provider = mockResourcesProvider
        )

        // Copy and change isLoggingEnabled
        val copiedConfigLoggingChanged = originalConfig.copy(isLoggingEnabled = true)

        assertEquals("Copied version should be same as original", originalConfig.version, copiedConfigLoggingChanged.version)
        assertTrue("Copied isLoggingEnabled should be true", copiedConfigLoggingChanged.isLoggingEnabled)
        assertEquals("Copied provider should be same as original", originalConfig.provider, copiedConfigLoggingChanged.provider)
        assertNotEquals("Copied instance should be different from original", originalConfig, copiedConfigLoggingChanged)

        // Ensure original is unchanged
        assertEquals("original-1.0", originalConfig.version)
        assertFalse(originalConfig.isLoggingEnabled)

        // Copy and change version
        val copiedConfigVersionChanged = originalConfig.copy(version = "copied-2.0")
        assertEquals("copied-2.0", copiedConfigVersionChanged.version)
        assertEquals("isLoggingEnabled should be same as original", originalConfig.isLoggingEnabled, copiedConfigVersionChanged.isLoggingEnabled)
        assertEquals("provider should be same as original", originalConfig.provider, copiedConfigVersionChanged.provider)
    }
}
