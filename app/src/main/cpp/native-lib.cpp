#include <jni.h>
#include <android/log.h>

#include <libxml/HTMLparser.h>
#include <libxml/HTMLtree.h>
#include "httplib.h"

#include <string>

// redefine printf to use the android log
#define printf(...) __android_log_print(ANDROID_LOG_DEBUG, "native-lib", __VA_ARGS__)

// taken from http://xmlsoft.org/examples/#tree1.c
static void print_element_names(xmlNode* a_node)
{
    xmlNode *cur_node = NULL;

    for (cur_node = a_node; cur_node; cur_node = cur_node->next) {
        if (cur_node->type == XML_ELEMENT_NODE) {
            printf("node type: Element, name: %s\n", cur_node->name);
        }

        print_element_names(cur_node->children);
    }
}

/*
 * If debug mode is ON:
 * Fetches schedule data from a cached HTML file.
 *
 * If debug mode is OFF:
 * Fetches the schedule data for the provided date from the Mizzou website.
 *
 * Parses the HTML data, and returns a string indicating whether or not the parsing was successful.
 * Prints all HTML nodes to the Android log.
*/

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_myapplication_MainActivity_getScheduleData(
        JNIEnv* env,
        jobject, /* this */
        jstring _date,
        jboolean _debugMode,
        jstring _cachedHtmlPath) {

    // convert java parameters to C++ parameters
    std::string date(env->GetStringUTFChars(_date, 0));
    bool debugMode = (bool)_debugMode;
    std::string cachedHtmlPath(env->GetStringUTFChars(_cachedHtmlPath, 0));

    // the output of the function (basic status info, displayed in TextView)
    std::string out;

    std::string resBody;
    if (!debugMode) {
        // send request to Mizzou server

        httplib::Client cli("https://dining.missouri.edu");
        cli.enable_server_certificate_verification(false);

        std::string path = "/locations/?hoursForDate=" + date;
        if (httplib::Result res = cli.Get(path)) {
            resBody = res->body;
            out += "success code: ";
            out += std::to_string(res->status) + "\n";
        } else {
            // connection error
            out += "error code: ";
            out += std::to_string((int)res.error()) + "\n";
            return env->NewStringUTF(out.c_str());
        }
    }

    xmlDoc* doc = NULL;

    if (!debugMode) {
        // read the document returned by the server
        doc = htmlReadMemory(resBody.c_str(), resBody.size() + 1, NULL, NULL, 0);
    }
    else {
        // read the cached document
        doc = htmlReadFile(cachedHtmlPath.c_str(), NULL, 0);
    }

    if (doc == NULL) {
        out += "could not parse\n";
        return env->NewStringUTF(out.c_str());
    }

    // parse the document
    xmlNode* root_element = xmlDocGetRootElement(doc);
    print_element_names(root_element);
    xmlFreeDoc(doc);
    xmlCleanupParser();

    out += "parsed, see logcat output!\n";
    return env->NewStringUTF(out.c_str());
}
