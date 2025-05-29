package com.example.ui.viewModels

import android.content.ContentResolver
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.voyager.data.repositories.ViewStateRepository
import com.voyager.data.repositories.XmlRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    // Rule for LiveData to execute synchronously
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test dispatcher for coroutines
    private val testDispatcher = StandardTestDispatcher() // Changed from TestCoroutineDispatcher

    @Mock
    private lateinit var mockXmlRepository: XmlRepository

    @Mock
    private lateinit var mockViewStateRepository: ViewStateRepository // Though not directly used, it's a constructor param

    @Mock
    private lateinit var mockContentResolver: ContentResolver

    @Mock
    private lateinit var mockUri: Uri

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set main dispatcher for viewModelScope
        viewModel = MainViewModel(mockXmlRepository, mockViewStateRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset main dispatcher
    }

    @Test
    fun `initial state verification`() {
        assertEquals("Choose File", viewModel.buttonText.value)
        assertEquals("Show Xml", viewModel.showXmlText.value)
        assertEquals(false, viewModel.enableShowing.value)
        assertNull(viewModel.selectedFile.value)
        assertNull(viewModel.parsedJson.value)
        assertNull(viewModel.createdFileUri.value)
        assertEquals(false, viewModel.isFileCreated.value)
        assertEquals(false, viewModel.isFileSelected.value)
        assertNull(viewModel.requestCreateFileEvent.value)
        assertEquals("Default EditText Value", viewModel.dynamicEditTextData.value)
        assertEquals("Default TextView Value from VM", viewModel.dynamicTextViewData.value)
    }

    @Test
    fun `setSelectedFileUri updates LiveData correctly`() {
        viewModel.setSelectedFileUri(mockUri)

        assertEquals(mockUri, viewModel.selectedFile.value)
        assertEquals("Convert File", viewModel.buttonText.value)
        assertTrue(viewModel.isFileSelected.value ?: false)
    }

    @Test
    fun `convertXmlToJson success updates LiveData`() = runTest {
        val testXml = "<test></test>"
        val testJson = "{\"test\": null}"
        val testFileName = "testfile.xml"
        val expectedOutputFileName = "testfile.json"
        val inputStream = ByteArrayInputStream(testXml.toByteArray())

        `when`(mockContentResolver.openInputStream(mockUri)).thenReturn(inputStream)
        `when`(mockXmlRepository.convertXmlToJson(any(InputStream::class.java))).thenReturn(org.json.JSONObject(testJson))
        // Mocking the callback behavior for getFileNameFromUri
        `when`(mockXmlRepository.getFileNameFromUri(eq(mockContentResolver), eq(mockUri), any()))
            .thenAnswer { invocation ->
                val callback = invocation.arguments[2] as (String) -> Unit
                callback.invoke(testFileName)
                return@thenAnswer Unit // Explicitly return Unit for a function returning Unit
            }


        viewModel.setSelectedFileUri(mockUri) // Set the URI first
        viewModel.convertXmlToJson(mockContentResolver)
        advanceUntilIdle() // Ensure coroutines complete

        assertEquals(testJson, viewModel.parsedJson.value)
        assertEquals(expectedOutputFileName, viewModel.requestCreateFileEvent.value)
    }

    @Test
    fun `convertXmlToJson error on openInputStream sets parsedJson to null`() = runTest {
        `when`(mockContentResolver.openInputStream(mockUri)).thenReturn(null)

        viewModel.setSelectedFileUri(mockUri)
        viewModel.convertXmlToJson(mockContentResolver)
        advanceUntilIdle()

        assertNull(viewModel.parsedJson.value)
        assertNull(viewModel.requestCreateFileEvent.value) // Should not be triggered if stream fails
    }

    @Test
    fun `convertXmlToJson error on repository conversion sets parsedJson to null`() = runTest {
        val testXml = "<test></test>"
        val inputStream = ByteArrayInputStream(testXml.toByteArray())

        `when`(mockContentResolver.openInputStream(mockUri)).thenReturn(inputStream)
        `when`(mockXmlRepository.convertXmlToJson(any(InputStream::class.java))).thenThrow(RuntimeException("Conversion error"))

        viewModel.setSelectedFileUri(mockUri)
        viewModel.convertXmlToJson(mockContentResolver)
        advanceUntilIdle()

        assertNull(viewModel.parsedJson.value)
         // Depending on desired behavior, requestCreateFileEvent might or might not be null.
        // Current MainViewModel logic would not call getFileNameFromUri if convertXmlToJson fails.
        assertNull(viewModel.requestCreateFileEvent.value)
    }


    @Test
    fun `writeToFile success updates LiveData`() = runTest {
        val testJsonContent = "{\"key\":\"value\"}"
        // Manually set _parsedJson.value - This is tricky as it's private.
        // A better way would be to have convertXmlToJson run successfully first,
        // or make _parsedJson temporarily internal/public for test, or use reflection.
        // For this test, we'll assume convertXmlToJson has run and populated _parsedJson.
        // We simulate this by directly setting it via a test-only method or reflection if needed.
        // However, since _parsedJson is private, we'll test the effect of writeToFile assuming it's populated.
        // Let's trigger convertXmlToJson successfully first.

        val convertInputStream = ByteArrayInputStream("<test></test>".toByteArray())
        `when`(mockContentResolver.openInputStream(mockUri)).thenReturn(convertInputStream)
        `when`(mockXmlRepository.convertXmlToJson(any(InputStream::class.java))).thenReturn(org.json.JSONObject(testJsonContent))
        `when`(mockXmlRepository.getFileNameFromUri(eq(mockContentResolver), eq(mockUri), any()))
            .thenAnswer { invocation ->
                (invocation.arguments[2] as (String) -> Unit).invoke("dummy.xml")
                 return@thenAnswer Unit
            }
        viewModel.setSelectedFileUri(mockUri)
        viewModel.convertXmlToJson(mockContentResolver)
        advanceUntilIdle() // Ensure _parsedJson is set

        // Now test writeToFile
        val outputStream = ByteArrayOutputStream()
        `when`(mockContentResolver.openOutputStream(mockUri)).thenReturn(outputStream)

        viewModel.writeToFile(mockUri, mockContentResolver)
        advanceUntilIdle()

        assertEquals(testJsonContent, outputStream.toString(Charsets.UTF_8.name()))
        assertEquals(mockUri, viewModel.createdFileUri.value)
        assertTrue(viewModel.isFileCreated.value ?: false)
        assertTrue(viewModel.enableShowing.value ?: false)
    }

    @Test(expected = IllegalStateException::class)
    fun `writeToFile throws IllegalStateException if parsedJson is null`() = runTest {
        // Ensure _parsedJson is null (initial state or after an error)
        // Directly testing the throw without setting up _parsedJson to be non-null
        assertNull(viewModel.parsedJson.value) // Verify it's null
        viewModel.writeToFile(mockUri, mockContentResolver) // This should throw
        advanceUntilIdle() // For consistency, though exception should be immediate
    }


    @Test
    fun `updateDynamicEditTextData updates LiveData`() {
        val newText = "New dynamic text"
        viewModel.updateDynamicEditTextData(newText)
        assertEquals(newText, viewModel.dynamicEditTextData.value)
    }

    @Test
    fun `creationRequestHandled sets event LiveData to null`() {
        // First, set the event to a non-null value by simulating convertXmlToJson
        val testXml = "<test></test>"
        val testJson = "{\"test\": null}"
        val testFileName = "testfile.xml"
        val inputStream = ByteArrayInputStream(testXml.toByteArray())

        `when`(mockContentResolver.openInputStream(mockUri)).thenReturn(inputStream)
        `when`(mockXmlRepository.convertXmlToJson(any(InputStream::class.java))).thenReturn(org.json.JSONObject(testJson))
        `when`(mockXmlRepository.getFileNameFromUri(eq(mockContentResolver), eq(mockUri), any()))
            .thenAnswer { invocation ->
                (invocation.arguments[2] as (String) -> Unit).invoke(testFileName)
                return@thenAnswer Unit
            }
        viewModel.setSelectedFileUri(mockUri)
        viewModel.convertXmlToJson(mockContentResolver)
        runCurrent() // Run pending coroutines to set the event

        assertNotNull(viewModel.requestCreateFileEvent.value) // Verify it's set

        viewModel.creationRequestHandled()
        assertNull(viewModel.requestCreateFileEvent.value)
    }

    // Helper for mocking any() with null safety for Kotlin
    private fun <T> any(type: Class<T>): T = Mockito.any(type)
}
