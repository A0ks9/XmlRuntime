/**
 * High-performance XML to JSON parser using Expat and RapidJSON.
 *
 * This module provides optimized XML parsing and JSON conversion with minimal memory overhead.
 * It uses efficient streaming parsing and optimized memory management.
 *
 * Key features:
 * - Incremental XML parsing
 * - Memory-efficient JSON generation
 * - Optimized string handling
 * - Thread-safe operations
 * - Comprehensive error handling
 *
 * Performance optimizations:
 * - Efficient buffer management
 * - Optimized memory allocation
 * - Minimized string operations
 * - Safe resource handling
 * - Fast JSON generation
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */

// Include necessary headers
#include <jni.h>
#include <string>
#include <vector>
#include <iostream>
#include <cstring>
#include <expat.h>
#include <rapidjson/prettywriter.h>
#include <rapidjson/stringbuffer.h>
#include <memory>
#include <stdexcept>
#include <android/log.h>

// Define logging macros for Android
#define LOG_TAG    "XMLParser"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Use namespaces for convenience
using namespace rapidjson;
using namespace std;

// Constants for optimization
namespace {
    constexpr int BUFFER_SIZE = 8192;  // Increased buffer size for better performance
    constexpr int INITIAL_VECTOR_CAPACITY = 16;  // Pre-allocate vector capacity
}

/**
 * Thread-local storage for parser state.
 * This structure maintains the state of the XML parsing process,
 * including the JSON writer, children tracking, and current depth.
 */
thread_local struct ParserState {
    unique_ptr<PrettyWriter<StringBuffer>> writer;  // JSON writer instance
    vector<bool> childrenStarted;                   // Tracks if children array has been started
    int depth = 0;                                  // Current XML depth
    StringBuffer buffer;                            // JSON output buffer

    // Constructor initializes the state
    ParserState() :
        writer(make_unique<PrettyWriter<StringBuffer>>(buffer)),
        childrenStarted(INITIAL_VECTOR_CAPACITY, false) {}
} g_state;

/**
 * Optimized function to remove namespace prefix.
 * Uses direct pointer arithmetic for better performance.
 *
 * @param str Input string with optional namespace prefix
 * @return Pointer to the substring after the colon, or original string if no colon found
 */
inline const char* removePrefixBeforeColon(const char* str) noexcept {
    const char* colonPtr = static_cast<const char*>(memchr(str, ':', strlen(str)));
    return colonPtr ? colonPtr + 1 : str;
}

/**
 * XML start element handler.
 * Processes the start of an XML element and writes corresponding JSON structure.
 * Handles attributes and prepares for child elements.
 *
 * @param userData User data pointer (unused)
 * @param name Element name
 * @param attributes Array of attribute name-value pairs
 */
void XMLCALL startElement(void* userData, const char* name, const char** attributes) noexcept {
    auto& state = g_state;
    LOGD("startElement: %s (Depth: %d)", name, state.depth);

    // Start children array if this is not the root element
    if (state.depth > 0 && !state.childrenStarted[state.depth - 1]) {
        state.writer->Key("children");
        state.writer->StartArray();
        state.childrenStarted[state.depth - 1] = true;
    }

    // Write element structure
    state.writer->StartObject();
    state.writer->Key("type");
    state.writer->String(name);

    // Process attributes if present
    if (attributes && attributes[0]) {
        state.writer->Key("attributes");
        state.writer->StartObject();

        // Write each attribute
        for (const char** attr = attributes; *attr; attr += 2) {
            state.writer->Key(removePrefixBeforeColon(*attr));
            state.writer->String(attr[1] ? attr[1] : "");
        }

        state.writer->EndObject();
    }

    // Prepare for potential children
    if (state.depth >= state.childrenStarted.size()) {
        state.childrenStarted.resize(state.depth + 1, false);
    }
    state.childrenStarted[state.depth] = false;
    ++state.depth;
}

/**
 * XML end element handler.
 * Closes the current element's JSON structure and handles children array.
 *
 * @param userData User data pointer (unused)
 * @param name Element name (unused)
 */
void XMLCALL endElement(void* userData, const char* name) noexcept {
    auto& state = g_state;
    LOGD("endElement: %s (Depth: %d)", name, state.depth);

    // Close children array if it was started
    if (state.childrenStarted[state.depth - 1]) {
        state.writer->EndArray();
    }
    
    // Close the current element
    state.writer->EndObject();
    state.childrenStarted.pop_back();
    --state.depth;
}

/**
 * JNI function for XML to JSON conversion.
 * Reads XML from Java InputStream and converts it to JSON string.
 * Uses incremental parsing for memory efficiency.
 *
 * @param env JNI environment
 * @param inputStream Java InputStream object containing XML data
 * @return JSON string as jstring, or empty string on error
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_voyager_core_data_utils_FileHelper_parseXML(JNIEnv* env, jobject /* this */, jobject inputStream) {
    LOGD("parseXML JNI function called");

    // Get InputStream class and read method
    jclass inputStreamClass = env->GetObjectClass(inputStream);
    jmethodID readMethod = env->GetMethodID(inputStreamClass, "read", "([B)I");
    if (!readMethod) {
        LOGE("Failed to find InputStream.read method");
        return env->NewStringUTF("");
    }

    // Allocate Java byte array for reading
    jbyteArray byteBuffer = env->NewByteArray(BUFFER_SIZE);
    if (!byteBuffer) {
        LOGE("Failed to allocate byte array");
        return env->NewStringUTF("");
    }

    // Create and configure XML parser
    unique_ptr<XML_ParserStruct, decltype(&XML_ParserFree)> parser(
        XML_ParserCreate(nullptr),
        XML_ParserFree
    );

    if (!parser) {
        LOGE("Error creating XML parser");
        env->DeleteLocalRef(byteBuffer);
        return env->NewStringUTF("");
    }

    // Set up element handlers
    XML_SetElementHandler(parser.get(), startElement, endElement);

    // Allocate native buffer for processing
    char nativeBuffer[BUFFER_SIZE];

    // Process XML data in chunks
    bool done = false;
    while (!done) {
        // Read chunk from InputStream
        jint bytesRead = env->CallIntMethod(inputStream, readMethod, byteBuffer);
        
        if (bytesRead < 0) {
            LOGE("Error reading from InputStream");
            break;
        }
        
        if (bytesRead == 0) {
            done = true;
            LOGD("Finished reading from InputStream");
        } else {
            LOGD("Read %d bytes from InputStream", bytesRead);
            
            // Copy data to native buffer and parse
            env->GetByteArrayRegion(byteBuffer, 0, bytesRead, reinterpret_cast<jbyte*>(nativeBuffer));
            if (XML_Parse(parser.get(), nativeBuffer, bytesRead, 0) == XML_STATUS_ERROR) {
                LOGE("XML Parse error: %s", XML_ErrorString(XML_GetErrorCode(parser.get())));
                env->DeleteLocalRef(byteBuffer);
                return env->NewStringUTF("");
            }
        }
    }

    // Finalize parsing
    if (XML_Parse(parser.get(), nativeBuffer, 0, 1) == XML_STATUS_ERROR) {
        LOGE("Final XML Parse error: %s", XML_ErrorString(XML_GetErrorCode(parser.get())));
        env->DeleteLocalRef(byteBuffer);
        return env->NewStringUTF("");
    }

    // Clean up and return result
    env->DeleteLocalRef(byteBuffer);
    const char* jsonString = g_state.buffer.GetString();
    LOGD("Generated JSON: %s", jsonString);
    return env->NewStringUTF(jsonString);
}