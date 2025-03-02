#include <cstdio>               // Standard I/O functions
#include <cstdlib>              // Standard library functions
#include <cstring>              // For string operations
#include <chrono>               // For high resolution timing
#include <sys/resource.h>       // For getrusage (memory usage)
#include <expat.h>              // Expat XML parser header
#include <fcntl.h>              // For open()
#include <sys/mman.h>           // For mmap()
#include <sys/stat.h>           // For fstat()
#include <unistd.h>             // For close()
#include <iostream>             // For std::cout and std::cerr
#include <string>               // For std::string
#include <vector>               // For std::vector
#include <rapidjson/prettywriter.h>  // For PrettyWriter (formatted JSON output)
#include <rapidjson/stringbuffer.h>  // For StringBuffer (in-memory output)
#include <jni.h>                // For JNI integration

using namespace std;
using namespace rapidjson;

// Global pointer for the JSON writer (accessible by callbacks)
PrettyWriter<StringBuffer> *g_writer = nullptr;
// Global vector to track if each element (by depth) has had its "children" array started
vector<bool> g_childrenStarted;
// Global depth counter (current XML nesting level)
int g_depth = 0;

/*
 * getMemoryUsage:
 * Returns the current maximum resident set size (memory usage) in kilobytes.
 */
size_t getMemoryUsage() {
    struct rusage usage{};
    getrusage(RUSAGE_SELF, &usage);
    return usage.ru_maxrss;
}

/*
 * startElement:
 * Called by Expat when a start element is encountered.
 * If the parent element hasn't yet had its "children" array started, do so now.
 */
void XMLCALL startElement(void *userData, const char *name, const char **attributes) {
    // If this element has a parent and the parent's children array is not started, start it.
    if (g_depth > 0 && !g_childrenStarted[g_depth - 1]) {
        g_writer->Key("children");
        g_writer->StartArray();
        g_childrenStarted[g_depth - 1] = true;
    }

    // Begin a new JSON object for this element.
    g_writer->StartObject();
    g_writer->Key("type");
    g_writer->String(name);

    // Write attributes (if any) in an "attrs" object.
    if (attributes && attributes[0]) {
        g_writer->Key("attributes");
        g_writer->StartObject();
        for (int i = 0; attributes[i]; i += 2) {
            g_writer->Key(attributes[i]);
            g_writer->String(attributes[i + 1] ? attributes[i + 1] : "");
        }
        g_writer->EndObject();
    }

    // Record that no children have been processed yet for this element.
    g_childrenStarted.push_back(false);
    ++g_depth;
}

/*
 * endElement:
 * Called by Expat when an end element is encountered.
 * Closes any started "children" array and the current JSON object.
 */
void XMLCALL endElement(void *userData, const char *name) {
    if (g_childrenStarted[g_depth - 1]) {
        g_writer->EndArray();
    }
    g_writer->EndObject();
    g_childrenStarted.pop_back();
    --g_depth;
}

/*
 * convertXmlToJsonString:
 * Given an XML file path, converts it to JSON and returns the JSON string.
 * Instead of writing to a file, it uses an in-memory StringBuffer.
 */
std::string convertXmlToJsonString(const char *xmlFile) {
    // Create a RapidJSON StringBuffer and PrettyWriter that writes into it.
    StringBuffer buffer;
    PrettyWriter<StringBuffer> writer(buffer);
    g_writer = &writer;

    // Create Expat parser.
    XML_Parser parser = XML_ParserCreate(nullptr);
    if (!parser) {
        cerr << "Error creating XML parser.\n";
        return "";
    }
    XML_SetElementHandler(parser, startElement, endElement);

    // Open the XML file.
    int fd = open(xmlFile, O_RDONLY);
    if (fd < 0) {
        cerr << "Error opening XML file for reading.\n";
        XML_ParserFree(parser);
        return "";
    }

    // Get file size.
    struct stat sb{};
    if (fstat(fd, &sb) < 0) {
        cerr << "Error getting file size.\n";
        close(fd);
        XML_ParserFree(parser);
        return "";
    }
    size_t fileSize = sb.st_size;

    // Memory-map the XML file.
    char *mapped = (char *) mmap(nullptr, fileSize, PROT_READ, MAP_PRIVATE, fd, 0);
    if (mapped == MAP_FAILED) {
        cerr << "Error mapping XML file.\n";
        close(fd);
        XML_ParserFree(parser);
        return "";
    }

    // Parse the XML file.
    if (XML_Parse(parser, mapped, fileSize, 1) == XML_STATUS_ERROR) {
        cerr << "XML Parse error: " << XML_ErrorString(XML_GetErrorCode(parser)) << "\n";
        munmap(mapped, fileSize);
        close(fd);
        XML_ParserFree(parser);
        return "";
    }

    // Cleanup resources.
    munmap(mapped, fileSize);
    close(fd);
    XML_ParserFree(parser);

    // Return the JSON string from the buffer.
    return {buffer.GetString(), buffer.GetSize()};
}

//
// JNI Wrapper: Expose the conversion function to Kotlin
//
extern "C" {

JNIEXPORT jstring JNICALL
Java_com_dynamic_utils_FileHelper_parseXML(JNIEnv *env, jobject /* this */, jstring xmlPath) {
    // Convert the jstring to a C-style string.
    const char *path = env->GetStringUTFChars(xmlPath, nullptr);

    // Call the conversion function to get the JSON string.
    std::string jsonResult = convertXmlToJsonString(path);

    // Release the UTF string.
    env->ReleaseStringUTFChars(xmlPath, path);

    // Return the JSON string to Kotlin.
    return env->NewStringUTF(jsonResult.c_str());
}

} // extern "C"
