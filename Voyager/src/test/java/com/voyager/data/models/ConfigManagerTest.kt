package com.voyager.data.models

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.lang.reflect.Field

@RunWith(MockitoJUnitRunner::class)
class ConfigManagerTest {

    // Field reference for resetting _config via reflection
    private lateinit var configField: Field

    @Before
    fun setUp() {
        // Obtain the private _config field for resetting
        try {
            configField = ConfigManager::class.java.getDeclaredField("_config")
            configField.isAccessible = true
        } catch (e: NoSuchFieldException) {
            throw RuntimeException("Failed to get _config field from ConfigManager for testing", e)
        }
        // Ensure _config is null before each test
        resetConfigManager()
    }

    @After
    fun tearDown() {
        // Reset _config to null after each test to ensure test independence
        resetConfigManager()
    }

    private fun resetConfigManager() {
        try {
            configField.set(ConfigManager, null)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Failed to reset ConfigManager._config via reflection", e)
        }
    }

    @Test
    fun `config access before initialization throws IllegalStateException`() {
        try {
            val config = ConfigManager.config
            fail("Accessing ConfigManager.config before initialization should throw IllegalStateException, but returned $config")
        } catch (e: IllegalStateException) {
            // Expected exception
            assertTrue(
                "Exception message should indicate not initialized",
                e.message!!.contains("VoyagerConfig not initialized")
            )
        }
    }

    @Test
    fun `initialize and config access after initialization returns correct config`() {
        val mockConfig = mock(VoyagerConfig::class.java)
        ConfigManager.initialize(mockConfig)

        val retrievedConfig = ConfigManager.config
        assertSame("ConfigManager.config should return the same instance passed to initialize()", mockConfig, retrievedConfig)
    }

    @Test
    fun `re-initialization attempt is ignored and original config is retained`() {
        val config1 = mock(VoyagerConfig::class.java)
        val config2 = mock(VoyagerConfig::class.java)

        // First initialization
        ConfigManager.initialize(config1)
        val retrievedConfig1 = ConfigManager.config
        assertSame("First initialization should set config1", config1, retrievedConfig1)

        // Attempt to re-initialize with config2
        ConfigManager.initialize(config2)
        val retrievedConfig2 = ConfigManager.config

        assertSame(
            "ConfigManager.config should still return config1 after re-initialization attempt",
            config1,
            retrievedConfig2
        )
        assertNotSame(
            "ConfigManager.config should not have been updated to config2",
            config2,
            retrievedConfig2
        )
    }

    // Optional: Test for logging warning on re-initialization.
    // This would require setting up a TestHandler for java.util.logging.Logger
    // or using a logging test library, which is more involved.
    // For now, the behavioral test (config not changing) is the primary focus.
    @Test
    fun `multiple initializations only sets config once`() {
        val config1 = mock(VoyagerConfig::class.java)
        val config2 = mock(VoyagerConfig::class.java)
        val config3 = mock(VoyagerConfig::class.java)

        ConfigManager.initialize(config1)
        ConfigManager.initialize(config2)
        ConfigManager.initialize(config3)

        assertSame("Only the first config should be set", config1, ConfigManager.config)
    }
}
