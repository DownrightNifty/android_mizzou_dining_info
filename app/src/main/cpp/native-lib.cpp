#include <jni.h>
#include <android/log.h>

#include <string>

#include <libxml/HTMLparser.h>
#include <libxml/HTMLtree.h>

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
 * Parses the XML document at the provided path, and returns a string indicating whether or not
 * parsing was successful. Prints all nodes to the Android log.
*/

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_myapplication_MainActivity_parseHTML(
        JNIEnv* env,
        jobject, /* this */
        jstring _htmlPath) {

    // convert Java string to C++ string
    std::string htmlPath(env->GetStringUTFChars(_htmlPath, 0));

    // parse the HTML document
    xmlDoc* doc = NULL;
    xmlNode* root_element = NULL;

    doc = htmlReadFile(htmlPath.c_str(), NULL, 0);

    if (doc == NULL) {
        return env->NewStringUTF("could not parse");
    }

    root_element = xmlDocGetRootElement(doc);
    print_element_names(root_element);
    xmlFreeDoc(doc);
    xmlCleanupParser();

    return env->NewStringUTF("parsed, see logcat output!");
}
