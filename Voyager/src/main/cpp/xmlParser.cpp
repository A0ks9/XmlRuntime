/**
 * High-performance XML to JSON parser using Expat and RapidJSON.
 *
 * This module provides optimized XML parsing and JSON conversion with minimal memory overhead.
 * It uses efficient streaming parsing and optimized memory management.
 *
 * Key Features:
 * - **Incremental XML Parsing:** Efficient streaming parsing of XML data
 * - **Memory-Efficient JSON Generation:** Optimized JSON output with minimal allocations
 * - **Thread Safety:** Thread-local storage for parser state
 * - **Error Handling:** Comprehensive error handling and logging
 * - **Performance Optimization:** Efficient buffer management and string handling
 *
 * Performance Optimizations:
 * - Efficient buffer management with optimized sizes
 * - Thread-local storage for parser state
 * - Direct pointer arithmetic for string operations
 * - Pre-allocated vector capacities
 * - Minimized object creation
 * - Safe resource handling
 *
 * Best Practices:
 * 1. Use appropriate buffer sizes for your use case
 * 2. Handle parser errors appropriately
 * 3. Consider memory usage with large XML files
 * 4. Monitor thread-local storage usage
 * 5. Use appropriate logging levels
 *
 * Example Usage:
 * ```cpp
 * // Parse XML from an input stream
 * jstring result = Java_com_voyager_utils_FileHelper_parseXML(env, inputStream);
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */

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

// Use namespaces for convenience
using namespace rapidjson;
using namespace std;

// Constants for optimization
constexpr int BUFFER_SIZE = 8192;  // Increased buffer size for better performance
constexpr int INITIAL_VECTOR_CAPACITY = 16;  // Pre-allocate vector capacity

/**
 * Thread-local storage for parser state to ensure thread safety.
 * Uses efficient memory management and pre-allocated buffers.
 */
thread_local struct ParserState {
    unique_ptr<PrettyWriter<StringBuffer>> writer;
    vector<bool> childrenStarted;
    int depth = 0;
    
    ParserState() : 
        writer(make_unique<PrettyWriter<StringBuffer>>(buffer)),
        childrenStarted(INITIAL_VECTOR_CAPACITY, false) {}
    
    StringBuffer buffer;
} g_state;

/**
 * Optimized function to remove namespace prefix.
 * Uses direct pointer arithmetic for better performance.
 *
 * Performance Considerations:
 * - Direct pointer arithmetic
 * - No string copying
 * - Minimal object creation
 * - Fast operation
 *
 * @param str Input string with optional namespace prefix
 * @return Pointer to the substring after the colon
 */
inline const char* removePrefixBeforeColon(const char* str) noexcept {
    const char* colonPtr = static_cast<const char*>(memchr(str, ':', strlen(str)));
    return colonPtr ? colonPtr + 1 : str;
}

/**
 * Optimized XML start element handler.
 * Uses direct buffer access and minimizes allocations.
 *
 * Performance Considerations:
 * - Efficient children array creation
 * - Direct buffer access
 * - Pre-allocated vector usage
 * - Minimal object creation
 *
 * @param userData User data pointer (unused)
 * @param name Element name
 * @param attributes Element attributes
 */
void XMLCALL startElement(void* userData, const char* name, const char** attributes) noexcept {
    auto& state = g_state;
    
    // Optimize children array creation
    if (state.depth > 0 && !state.childrenStarted[state.depth - 1]) {
        state.writer->Key("children");
        state.writer->StartArray();
        state.childrenStarted[state.depth - 1] = true;
    }
    
    // Write element with optimized attribute handling
    state.writer->StartObject();
    state.writer->Key("type");
    state.writer->String(name);

    if (attributes && attributes[0]) {
        state.writer->Key("attributes");
        state.writer->StartObject();
        
        // Use direct pointer access for better performance
        for (const char** attr = attributes; *attr; attr += 2) {
            state.writer->Key(removePrefixBeforeColon(*attr));
            state.writer->String(attr[1] ? attr[1] : "");
        }
        
        state.writer->EndObject();
    }
    
    // Pre-allocate vector if needed
    if (state.depth >= state.childrenStarted.size()) {
        state.childrenStarted.resize(state.depth + 1, false);
    }
    state.childrenStarted[state.depth] = false;
    ++state.depth;
}

/**
 * Optimized XML end element handler.
 * Uses direct buffer access for better performance.
 *
 * Performance Considerations:
 * - Direct buffer access
 * - Efficient vector operations
 * - Minimal object creation
 * - Fast operation
 *
 * @param userData User data pointer (unused)
 * @param name Element name (unused)
 */
void XMLCALL endElement(void* userData, const char* name) noexcept {
    auto& state = g_state;
    
    if (state.childrenStarted[state.depth - 1]) {
        state.writer->EndArray();
    }
    state.writer->EndObject();
    state.childrenStarted.pop_back();
    --state.depth;
}

/**
 * JNI function for XML to JSON conversion with optimized memory management.
 * Uses incremental parsing and efficient buffer handling.
 *
 * Performance Considerations:
 * - Incremental parsing
 * - Efficient buffer management
 * - Safe resource handling
 * - Error handling
 * - Memory cleanup
 *
 * @param env JNI environment
 * @param inputStream Java InputStream object
 * @return JSON string as jstring
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_voyager_core_data_utils_FileHelper_parseXML(JNIEnv* env, jobject /* this */, jobject inputStream) {
    // Get InputStream class and read method
    jclass inputStreamClass = env->GetObjectClass(inputStream);
    jmethodID readMethod = env->GetMethodID(inputStreamClass, "read", "([B)I");
    if (!readMethod) {
        cerr << "Failed to find InputStream.read method." << endl;
        return env->NewStringUTF("");
    }

    // Allocate Java byte array
    jbyteArray byteBuffer = env->NewByteArray(BUFFER_SIZE);
    if (!byteBuffer) {
        cerr << "Failed to allocate byte array." << endl;
        return env->NewStringUTF("");
    }

    // Create XML parser with error handling
    unique_ptr<XML_ParserStruct, decltype(&XML_ParserFree)> parser(
        XML_ParserCreate(nullptr),
        XML_ParserFree
    );
    
    if (!parser) {
        cerr << "Error creating XML parser." << endl;
        env->DeleteLocalRef(byteBuffer);
        return env->NewStringUTF("");
    }

    // Set up element handlers
    XML_SetElementHandler(parser.get(), startElement, endElement);

    // Allocate native buffer on stack
    char nativeBuffer[BUFFER_SIZE];

    // Incremental parsing with optimized error handling
    bool done = false;
    while (!done) {
        jint bytesRead = env->CallIntMethod(inputStream, readMethod, byteBuffer);
        if (bytesRead < 0) break;
        if (bytesRead == 0) {
            done = true;
        } else {
            env->GetByteArrayRegion(byteBuffer, 0, bytesRead, reinterpret_cast<jbyte*>(nativeBuffer));
            if (XML_Parse(parser.get(), nativeBuffer, bytesRead, 0) == XML_STATUS_ERROR) {
                cerr << "XML Parse error: " << XML_ErrorString(XML_GetErrorCode(parser.get())) << endl;
                env->DeleteLocalRef(byteBuffer);
                return env->NewStringUTF("");
            }
        }
    }

    // Finalize parsing
    if (XML_Parse(parser.get(), nativeBuffer, 0, 1) == XML_STATUS_ERROR) {
        cerr << "Final XML Parse error: " << XML_ErrorString(XML_GetErrorCode(parser.get())) << endl;
        env->DeleteLocalRef(byteBuffer);
        return env->NewStringUTF("");
    }

    // Clean up and return result
    env->DeleteLocalRef(byteBuffer);
    return env->NewStringUTF(g_state.buffer.GetString());
}
