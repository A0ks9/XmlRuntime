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
#include <vector>
#include <cstring>
#include <expat.h>
#include <rapidjson/prettywriter.h>
#include <rapidjson/stringbuffer.h>
#include <android/log.h>
#include <cstdint>
#include <string>
#include <map>

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
    constexpr int SHA256_DIGEST_LENGTH = 32;  // SHA256 produces 32 bytes
}

// SHA256 implementation
class SHA256 {
private:
    uint32_t state[8];
    uint8_t data[64];
    size_t datalen;
    uint64_t bitlen;

    static const uint32_t k[64];

    static uint32_t rotr(uint32_t x, uint32_t n) {
        return (x >> n) | (x << (32 - n));
    }

    static uint32_t choose(uint32_t e, uint32_t f, uint32_t g) {
        return (e & f) ^ (~e & g);
    }

    static uint32_t majority(uint32_t a, uint32_t b, uint32_t c) {
        return (a & (b | c)) | (b & c);
    }

    static uint32_t ep0(uint32_t x) {
        return rotr(x, 2) ^ rotr(x, 13) ^ rotr(x, 22);
    }

    static uint32_t ep1(uint32_t x) {
        return rotr(x, 6) ^ rotr(x, 11) ^ rotr(x, 25);
    }

    static uint32_t sig0(uint32_t x) {
        return rotr(x, 7) ^ rotr(x, 18) ^ (x >> 3);
    }

    static uint32_t sig1(uint32_t x) {
        return rotr(x, 17) ^ rotr(x, 19) ^ (x >> 10);
    }

    void transform() {
        uint32_t a, b, c, d, e, f, g, h, i, j, t1, t2, m[64];

        for (i = 0, j = 0; i < 16; ++i, j += 4)
            m[i] = (data[j] << 24) | (data[j + 1] << 16) | (data[j + 2] << 8) | (data[j + 3]);
        for (; i < 64; ++i)
            m[i] = sig1(m[i - 2]) + m[i - 7] + sig0(m[i - 15]) + m[i - 16];

        a = state[0];
        b = state[1];
        c = state[2];
        d = state[3];
        e = state[4];
        f = state[5];
        g = state[6];
        h = state[7];

        for (i = 0; i < 64; ++i) {
            t1 = h + ep1(e) + choose(e, f, g) + k[i] + m[i];
            t2 = ep0(a) + majority(a, b, c);
            h = g;
            g = f;
            f = e;
            e = d + t1;
            d = c;
            c = b;
            b = a;
            a = t1 + t2;
        }

        state[0] += a;
        state[1] += b;
        state[2] += c;
        state[3] += d;
        state[4] += e;
        state[5] += f;
        state[6] += g;
        state[7] += h;
    }

public:
    SHA256() {
        reset();
    }

    void reset() {
        datalen = 0;
        bitlen = 0;
        state[0] = 0x6a09e667;
        state[1] = 0xbb67ae85;
        state[2] = 0x3c6ef372;
        state[3] = 0xa54ff53a;
        state[4] = 0x510e527f;
        state[5] = 0x9b05688c;
        state[6] = 0x1f83d9ab;
        state[7] = 0x5be0cd19;
    }

    void update(const uint8_t *inputData, size_t len) {
        for (size_t i = 0; i < len; ++i) {
            this->data[datalen] = inputData[i];
            datalen++;
            if (datalen == 64) {
                transform();
                bitlen += 512;
                datalen = 0;
            }
        }
    }

    void final(uint8_t *hash) {
        uint32_t i = datalen;

        if (datalen < 56) {
            data[i++] = 0x80;
            while (i < 56)
                data[i++] = 0x00;
        } else {
            data[i++] = 0x80;
            while (i < 64)
                data[i++] = 0x00;
            transform();
            memset(data, 0, 56);
        }

        bitlen += datalen * 8;
        data[63] = bitlen;
        data[62] = bitlen >> 8;
        data[61] = bitlen >> 16;
        data[60] = bitlen >> 24;
        data[59] = bitlen >> 32;
        data[58] = bitlen >> 40;
        data[57] = bitlen >> 48;
        data[56] = bitlen >> 56;
        transform();

        for (i = 0; i < 4; ++i) {
            hash[i] = (state[0] >> (24 - i * 8)) & 0x000000ff;
            hash[i + 4] = (state[1] >> (24 - i * 8)) & 0x000000ff;
            hash[i + 8] = (state[2] >> (24 - i * 8)) & 0x000000ff;
            hash[i + 12] = (state[3] >> (24 - i * 8)) & 0x000000ff;
            hash[i + 16] = (state[4] >> (24 - i * 8)) & 0x000000ff;
            hash[i + 20] = (state[5] >> (24 - i * 8)) & 0x000000ff;
            hash[i + 24] = (state[6] >> (24 - i * 8)) & 0x000000ff;
            hash[i + 28] = (state[7] >> (24 - i * 8)) & 0x000000ff;
        }
    }
};

// SHA256 constants
const uint32_t SHA256::k[64] = {0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b,
                                0x59f111f1, 0x923f82a4, 0xab1c5ed5, 0xd807aa98, 0x12835b01,
                                0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7,
                                0xc19bf174, 0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
                                0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da, 0x983e5152,
                                0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147,
                                0x06ca6351, 0x14292967, 0x27b70a85, 0x2e1b2138, 0x4d2c6dfc,
                                0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
                                0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819,
                                0xd6990624, 0xf40e3585, 0x106aa070, 0x19a4c116, 0x1e376c08,
                                0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f,
                                0x682e6ff3, 0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
                                0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2};

/**
 * Thread-local storage for parser state.
 * This structure maintains the state of the XML parsing process.
 */
thread_local struct ParserState {
    JNIEnv *env;
    jobject tokenStream;
    jmethodID onTokenMethod;
    jmethodID onCompleteMethod;
    SHA256 sha256;
    uint8_t hash[SHA256_DIGEST_LENGTH];
    string currentText;

    // Constructor initializes the state
    ParserState() : env(nullptr), tokenStream(nullptr), onTokenMethod(nullptr),
                    onCompleteMethod(nullptr) {
        sha256.reset();
    }
} g_state;

// Helper function to create a Java Map from attributes
jobject createAttributeMap(JNIEnv *env, const char **attributes) {
    jclass mapClass = env->FindClass("androidx/collection/ArrayMap");
    jmethodID mapConstructor = env->GetMethodID(mapClass, "<init>", "()V");
    jmethodID putMethod = env->GetMethodID(mapClass, "put",
                                           "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    jobject map = env->NewObject(mapClass, mapConstructor);

    for (const char **attr = attributes; *attr; attr += 2) {
        const char *key = *attr;
        while (*key && *key != ':') key++;
        if (*key) key++;
        jstring keyStr = env->NewStringUTF(key);
        jstring value = env->NewStringUTF(attr[1] ? attr[1] : "");
        env->CallObjectMethod(map, putMethod, keyStr, value);
        env->DeleteLocalRef(keyStr);
        env->DeleteLocalRef(value);
    }

    return map;
}

// Helper function to create a StartElement token
void createStartElementToken(const char *name, const char **attributes) {
    JNIEnv *env = g_state.env;

    // Create attribute map
    jobject attrMap = createAttributeMap(env, attributes);

    // Create StartElement token
    jclass tokenClass = env->FindClass("com/voyager/core/data/utils/XmlToken$StartElement");
    jmethodID constructor = env->GetMethodID(tokenClass, "<init>",
                                             "(Ljava/lang/String;Landroidx/collection/ArrayMap;)V");

    jstring typeStr = env->NewStringUTF(name);
    jobject token = env->NewObject(tokenClass, constructor, typeStr, attrMap);

    // Call onToken
    env->CallVoidMethod(g_state.tokenStream, g_state.onTokenMethod, token);

    // Clean up
    env->DeleteLocalRef(token);
    env->DeleteLocalRef(typeStr);
    env->DeleteLocalRef(attrMap);
}

// Helper function to create an EndElement token
void createEndElementToken(const char *name) {
    JNIEnv *env = g_state.env;

    jclass tokenClass = env->FindClass("com/voyager/core/data/utils/XmlToken$EndElement");
    jmethodID constructor = env->GetMethodID(tokenClass, "<init>", "(Ljava/lang/String;)V");

    jstring typeStr = env->NewStringUTF(name);
    jobject token = env->NewObject(tokenClass, constructor, typeStr);

    // Call onToken
    env->CallVoidMethod(g_state.tokenStream, g_state.onTokenMethod, token);

    // Clean up
    env->DeleteLocalRef(token);
    env->DeleteLocalRef(typeStr);
}

// Helper function to create a Text token
void createTextToken(const string &text) {
    if (text.empty()) return;

    JNIEnv *env = g_state.env;

    jclass tokenClass = env->FindClass("com/voyager/core/data/utils/XmlToken$Text");
    jmethodID constructor = env->GetMethodID(tokenClass, "<init>", "(Ljava/lang/String;)V");

    jstring textStr = env->NewStringUTF(text.c_str());
    jobject token = env->NewObject(tokenClass, constructor, textStr);

    // Call onToken
    env->CallVoidMethod(g_state.tokenStream, g_state.onTokenMethod, token);

    // Clean up
    env->DeleteLocalRef(token);
    env->DeleteLocalRef(textStr);
}

// XML start element handler
void XMLCALL startElement(void *userData, const char *name, const char **attributes) {
    // Send any accumulated text
    if (!g_state.currentText.empty()) {
        createTextToken(g_state.currentText);
        g_state.currentText.clear();
    }

    createStartElementToken(name, attributes);
}

// XML end element handler
void XMLCALL endElement(void *userData, const char *name) {
    // Send any accumulated text
    if (!g_state.currentText.empty()) {
        createTextToken(g_state.currentText);
        g_state.currentText.clear();
    }

    createEndElementToken(name);
}

// XML character data handler
void XMLCALL characterData(void *userData, const char *s, int len) {
    g_state.currentText.append(s, len);
}

extern "C" JNIEXPORT void JNICALL
Java_com_voyager_core_data_utils_FileHelper_parseXML(JNIEnv *env, jobject /* this */,
                                                     jobject inputStream, jobject tokenStream) {
    LOGD("parseXML JNI function called");

    // Store JNI references
    g_state.env = env;
    g_state.tokenStream = env->NewGlobalRef(tokenStream);

    // Prepare variables at the top to avoid goto over declaration
    jbyteArray hashArray = nullptr;
    bool done = false;
    jbyteArray byteBuffer = nullptr;

    // Create and configure XML parser
    unique_ptr<XML_ParserStruct, decltype(&XML_ParserFree)> parser(XML_ParserCreate(nullptr),
                                                                   XML_ParserFree);

    // Get token stream methods
    jclass tokenStreamClass = env->GetObjectClass(tokenStream);
    g_state.onTokenMethod = env->GetMethodID(tokenStreamClass, "onToken",
                                             "(Lcom/voyager/core/data/utils/XmlToken;)V");
    g_state.onCompleteMethod = env->GetMethodID(tokenStreamClass, "onComplete", "([B)V");

    // Get InputStream class and read method
    jclass inputStreamClass = env->GetObjectClass(inputStream);
    jmethodID readMethod = env->GetMethodID(inputStreamClass, "read", "([B)I");
    if (!readMethod) {
        LOGE("Failed to find InputStream.read method");
        goto cleanup;
    }

    // Allocate Java byte array for reading
    byteBuffer = env->NewByteArray(BUFFER_SIZE);
    if (!byteBuffer) {
        LOGE("Failed to allocate byte array");
        goto cleanup;
    }

    if (!parser) {
        LOGE("Error creating XML parser");
        env->DeleteLocalRef(byteBuffer);
        goto cleanup;
    }

    // Set up element handlers
    XML_SetElementHandler(parser.get(), startElement, endElement);
    XML_SetCharacterDataHandler(parser.get(), characterData);

    // Allocate native buffer for processing
    char nativeBuffer[BUFFER_SIZE];

    // Process XML data in chunks
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
            env->GetByteArrayRegion(byteBuffer, 0, bytesRead,
                                    reinterpret_cast<jbyte *>(nativeBuffer));

            // Update SHA256 hash
            g_state.sha256.update(reinterpret_cast<uint8_t *>(nativeBuffer), bytesRead);

            if (XML_Parse(parser.get(), nativeBuffer, bytesRead, 0) == XML_STATUS_ERROR) {
                LOGE("XML Parse error: %s", XML_ErrorString(XML_GetErrorCode(parser.get())));
                env->DeleteLocalRef(byteBuffer);
                goto cleanup;
            }
        }
    }

    // Finalize parsing
    if (XML_Parse(parser.get(), nativeBuffer, 0, 1) == XML_STATUS_ERROR) {
        LOGE("Final XML Parse error: %s", XML_ErrorString(XML_GetErrorCode(parser.get())));
        env->DeleteLocalRef(byteBuffer);
        goto cleanup;
    }

    // Finalize SHA256 hash
    g_state.sha256.final(g_state.hash);

    // Create byte array for hash
    hashArray = env->NewByteArray(SHA256_DIGEST_LENGTH);
    if (!hashArray) {
        LOGE("Failed to create hash byte array");
        env->DeleteLocalRef(byteBuffer);
        goto cleanup;
    }

    // Copy hash to Java byte array
    env->SetByteArrayRegion(hashArray, 0, SHA256_DIGEST_LENGTH,
                            reinterpret_cast<jbyte *>(g_state.hash));

    // Call onComplete with hash
    env->CallVoidMethod(tokenStream, g_state.onCompleteMethod, hashArray);

    // Clean up
    env->DeleteLocalRef(hashArray);
    env->DeleteLocalRef(byteBuffer);

    cleanup:
    // Clean up global references
    if (g_state.tokenStream) {
        env->DeleteGlobalRef(g_state.tokenStream);
    }
}