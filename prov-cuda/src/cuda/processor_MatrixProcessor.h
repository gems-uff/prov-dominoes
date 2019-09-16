/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class processor_MatrixProcessor */

#ifndef _Included_processor_MatrixProcessor
#define _Included_processor_MatrixProcessor
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     processor_MatrixProcessor
 * Method:    resetGPU
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_resetGPU
  (JNIEnv *, jclass, jint);

/*
 * Class:     processor_MatrixProcessor
 * Method:    isGPUEnabled
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_processor_MatrixProcessor_isGPUEnabled
  (JNIEnv *, jclass);

/*
 * Class:     processor_MatrixProcessor
 * Method:    getDeviceCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_processor_MatrixProcessor_getDeviceCount
  (JNIEnv *, jclass);

/*
 * Class:     processor_MatrixProcessor
 * Method:    getSparseData
 * Signature: (J)[Lprocessor/Cell;
 */
JNIEXPORT jobjectArray JNICALL Java_processor_MatrixProcessor_getSparseData
  (JNIEnv *, jclass, jlong);

/*
 * Class:     processor_MatrixProcessor
 * Method:    getData
 * Signature: (J[I[I)[Lprocessor/Cell;
 */
JNIEXPORT jobjectArray JNICALL Java_processor_MatrixProcessor_getData
  (JNIEnv *, jclass, jlong, jintArray, jintArray);

/*
 * Class:     processor_MatrixProcessor
 * Method:    createMatrixData
 * Signature: (IIZ)J
 */
JNIEXPORT jlong JNICALL Java_processor_MatrixProcessor_createMatrixData
  (JNIEnv *, jclass, jint, jint, jboolean);

/*
 * Class:     processor_MatrixProcessor
 * Method:    deleteMatrixData
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_processor_MatrixProcessor_deleteMatrixData
  (JNIEnv *, jclass, jlong);

/*
 * Class:     processor_MatrixProcessor
 * Method:    setData
 * Signature: (J[I[I[F)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_setData__J_3I_3I_3F
  (JNIEnv *, jclass, jlong, jintArray, jintArray, jfloatArray);

/*
 * Class:     processor_MatrixProcessor
 * Method:    setData
 * Signature: (J[F)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_setData__J_3F
  (JNIEnv *, jclass, jlong, jfloatArray);

/*
 * Class:     processor_MatrixProcessor
 * Method:    multiply
 * Signature: (JJJZ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_multiply
  (JNIEnv *, jclass, jlong, jlong, jlong, jboolean);

/*
 * Class:     processor_MatrixProcessor
 * Method:    transpose
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_transpose
  (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     processor_MatrixProcessor
 * Method:    reduceRow
 * Signature: (JJZ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_reduceRow
  (JNIEnv *, jclass, jlong, jlong, jboolean);

/*
 * Class:     processor_MatrixProcessor
 * Method:    confidence
 * Signature: (JJZ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_confidence
  (JNIEnv *, jclass, jlong, jlong, jboolean);

/*
 * Class:     processor_MatrixProcessor
 * Method:    mean
 * Signature: (JJZ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_mean
  (JNIEnv *, jclass, jlong, jlong, jboolean);

/*
 * Class:     processor_MatrixProcessor
 * Method:    meanSD
 * Signature: (JJZ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_meanSD
  (JNIEnv *, jclass, jlong, jlong, jboolean);

/*
 * Class:     processor_MatrixProcessor
 * Method:    standardScore
 * Signature: (JJZ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_standardScore
  (JNIEnv *, jclass, jlong, jlong, jboolean);

/*
 * Class:     processor_MatrixProcessor
 * Method:    standardDeviation
 * Signature: (JJZ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_standardDeviation
  (JNIEnv *, jclass, jlong, jlong, jboolean);

/*
 * Class:     processor_MatrixProcessor
 * Method:    getMin
 * Signature: (J)F
 */
JNIEXPORT jfloat JNICALL Java_processor_MatrixProcessor_getMin
  (JNIEnv *, jclass, jlong);

/*
 * Class:     processor_MatrixProcessor
 * Method:    getMax
 * Signature: (J)F
 */
JNIEXPORT jfloat JNICALL Java_processor_MatrixProcessor_getMax
  (JNIEnv *, jclass, jlong);

/*
 * Class:     processor_MatrixProcessor
 * Method:    binarize
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_binarize
  (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     processor_MatrixProcessor
 * Method:    invert
 * Signature: (IJJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_invert
  (JNIEnv *, jclass, jint, jlong, jlong);

/*
 * Class:     processor_MatrixProcessor
 * Method:    diagonalize
 * Signature: (IJJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_diagonalize
  (JNIEnv *, jclass, jint, jlong, jlong);

/*
 * Class:     processor_MatrixProcessor
 * Method:    upperDiagonal
 * Signature: (IJJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_upperDiagonal
  (JNIEnv *, jclass, jint, jlong, jlong);

/*
 * Class:     processor_MatrixProcessor
 * Method:    lowerDiagonal
 * Signature: (IJJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_lowerDiagonal
  (JNIEnv *, jclass, jint, jlong, jlong);

/*
 * Class:     processor_MatrixProcessor
 * Method:    transitiveClosure
 * Signature: (IJJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_transitiveClosure
  (JNIEnv *, jclass, jint, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif
