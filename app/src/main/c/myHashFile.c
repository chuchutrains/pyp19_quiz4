//
// Created by ChuHan on 23-Mar-20.
//

#include <stdio.h> // c library, must have
#include <jni.h> // jni library
#include "pbkdf2-sha256.c" // import this file so we can access its function

JNIEXPORT jbyteArray JNICALL
// <Java><app id><activity name><function name>, if wrong format alt+enter for naming suggestion
Java_com_example_pyp19_1quiz4_MainActivity_hashMe(JNIEnv *env, jobject obj, // jni input parameter, must have
        // self declare input parameter, input pass in from kotlin
        jstring kotlinMsg, jint kotlinMsgLen,
        jstring kotlinSalt, jint kotlinSaltLen,
        jint kotlinIterations, jint kotlinByteLen)
{
    // convert j-type to c-type, eg. jint to int, jstring to char* (c dont have type string)
    char *nativeMsg = (char *) (*env)->GetStringUTFChars(env, kotlinMsg, 0); // Returns a pointer to an array of bytes representing the string; similiar to val nativeMsg = kotlinMsg.toString()
    int nativeMsgLen = kotlinMsgLen;
    char *nativeSalt = (char *) (*env)->GetStringUTFChars(env, kotlinSalt, 0); // Returns a pointer to an array of bytes representing the string
    int nativeSaltLen = kotlinSaltLen;
    int nativeIterations = kotlinIterations;
    int nativeByteLen = kotlinByteLen;

    char *nativeOutput = malloc((size_t) nativeByteLen); // allocate memory
    // call PKCS5_PBKDF2_HMAC function from pbkdf2-sha256.c
    PKCS5_PBKDF2_HMAC((unsigned char *) nativeMsg, (size_t) nativeMsgLen,
                      (unsigned char *) nativeSalt, (size_t) nativeSaltLen,
                      (const unsigned long) nativeIterations, (const unsigned long) nativeByteLen,
                      (unsigned char *) nativeOutput);

    jbyteArray byteArray = (*env)->NewByteArray(env, nativeByteLen); // initialize byteArray; similiar to char *byteArray[nativeByteLen];
    (*env)->SetByteArrayRegion(env, byteArray, 0, nativeByteLen, (const jbyte *) nativeOutput); // convert c-type array to j-type bytearray
    (*env)->ReleaseByteArrayElements(env, byteArray, (jbyte *) nativeOutput, 0); // free nativeOutput memory
    return byteArray; // return type jbyteArray
}