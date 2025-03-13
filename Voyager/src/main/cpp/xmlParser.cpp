#include <jni.h>
#include <string>
#include <vector>
#include <iostream>
#include <cstring>
#include <expat.h>
#include <rapidjson/prettywriter.h>
#include <rapidjson/stringbuffer.h>

// Use namespaces for convenience.
using namespace rapidjson;
using namespace std;

// Global pointer to the JSON writer (used in XML callbacks)
PrettyWriter<StringBuffer>* g_writer = nullptr;
// Global vector tracking if a "children" JSON array was started at each XML depth
vector<bool> g_childrenStarted;
// Global depth counter for current XML nesting level
int g_depth = 0;

/*
 * removePrefixBeforeColon:
 * Removes any namespace prefix by returning the substring after a colon.
 */
const char* removePrefixBeforeColon(const char* str) {
    const char* colonPtr = static_cast<const char*>(memchr(str, ':', strlen(str)));
    return (colonPtr != nullptr) ? (colonPtr + 1) : str;
}

/*
 * startElement:
 * Callback called by Expat when an XML start element is encountered.
 * It writes the element and its attributes into the JSON writer.
 */
void XMLCALL startElement(void* userData, const char* name, const char** attributes) {
    if (g_depth > 0 && !g_childrenStarted[g_depth - 1]) {
        g_writer->Key("children");
        g_writer->StartArray();
        g_childrenStarted[g_depth - 1] = true;
    }
    g_writer->StartObject();
    g_writer->Key("type");
    g_writer->String(name);

    if (attributes && attributes[0]) {
        g_writer->Key("attributes");
        g_writer->StartObject();
        for (int i = 0; attributes[i]; i += 2) {
            g_writer->Key(removePrefixBeforeColon(attributes[i]));
            g_writer->String(attributes[i + 1] ? attributes[i + 1] : "");
        }
        g_writer->EndObject();
    }
    g_childrenStarted.push_back(false);
    ++g_depth;
}

/*
 * endElement:
 * Callback called by Expat when an XML end element is encountered.
 * It closes any started arrays and the JSON object.
 */
void XMLCALL endElement(void* userData, const char* name) {
    if (g_childrenStarted[g_depth - 1]) {
        g_writer->EndArray();
    }
    g_writer->EndObject();
    g_childrenStarted.pop_back();
    --g_depth;
}

/*
 * JNI function optimized to read from an InputStream incrementally.
 * It reads fixed-size chunks from the Java InputStream, feeds them to Expat,
 * and converts XML to JSON using RapidJSON.
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_voyager_utils_FileHelper_parseXML(JNIEnv* env, jobject /* this */, jobject inputStream) {
    // Retrieve the InputStream class and its read method.
    jclass inputStreamClass = env->GetObjectClass(inputStream);
    jmethodID readMethod = env->GetMethodID(inputStreamClass, "read", "([B)I");
    if (!readMethod) {
        std::cerr << "Failed to find InputStream.read method." << std::endl;
        return env->NewStringUTF("");
    }

    // Use a larger buffer (e.g., 4096 bytes) to reduce JNI call overhead.
    const int bufferSize = 4096;
    jbyteArray byteBuffer = env->NewByteArray(bufferSize);
    if (!byteBuffer) {
        std::cerr << "Failed to allocate byte array." << std::endl;
        return env->NewStringUTF("");
    }

    // Create the Expat XML parser.
    XML_Parser parser = XML_ParserCreate(nullptr);
    if (!parser) {
        std::cerr << "Error creating XML parser." << std::endl;
        env->DeleteLocalRef(byteBuffer);
        return env->NewStringUTF("");
    }
    XML_SetElementHandler(parser, startElement, endElement);

    // Set up RapidJSON's StringBuffer and PrettyWriter for JSON output.
    StringBuffer jsonBuffer;
    PrettyWriter<StringBuffer> writer(jsonBuffer);
    g_writer = &writer;  // Make the writer accessible to callbacks.

    // Allocate a native buffer on the stack.
    char nativeBuffer[bufferSize];

    // Incremental parsing: read and process XML in chunks.
    bool done = false;
    while (!done) {
        // Call read() on the InputStream to fill the Java byte array.
        jint bytesRead = env->CallIntMethod(inputStream, readMethod, byteBuffer);
        if (bytesRead < 0) {
            // Error reading stream.
            break;
        }
        if (bytesRead == 0) {
            // End of stream.
            done = true;
        } else {
            // Copy bytes from the Java array directly into the native buffer.
            env->GetByteArrayRegion(byteBuffer, 0, bytesRead, reinterpret_cast<jbyte*>(nativeBuffer));
            // Parse this chunk. '0' indicates that more data follows.
            if (XML_Parse(parser, nativeBuffer, bytesRead, 0) == XML_STATUS_ERROR) {
                std::cerr << "XML Parse error: "
                          << XML_ErrorString(XML_GetErrorCode(parser)) << std::endl;
                XML_ParserFree(parser);
                env->DeleteLocalRef(byteBuffer);
                return env->NewStringUTF("");
            }
        }
    }
    // Finalize parsing. An empty chunk with the isFinal flag set to 1.
    if (XML_Parse(parser, nativeBuffer, 0, 1) == XML_STATUS_ERROR) {
        std::cerr << "Final XML Parse error: "
                  << XML_ErrorString(XML_GetErrorCode(parser)) << std::endl;
        XML_ParserFree(parser);
        env->DeleteLocalRef(byteBuffer);
        return env->NewStringUTF("");
    }

    // Clean up parser and local references.
    XML_ParserFree(parser);
    env->DeleteLocalRef(byteBuffer);

    // Return the JSON result as a new jstring.
    return env->NewStringUTF(jsonBuffer.GetString());
}
