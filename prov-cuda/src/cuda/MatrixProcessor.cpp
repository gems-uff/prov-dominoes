#include <jni.h>
#include "processor_MatrixProcessor.h"
#include <stdio.h>
#include <Eigen/SparseCore>

#include <vector>

typedef Eigen::SparseMatrix<float, Eigen::RowMajor> SpMatf;
typedef Eigen::Triplet<float> Tf;

struct CellInfo {
	int row;
	int col;
	float value;
};

// Código implementado externamente via CUDA.
extern "C" {

	void g_ResetAndSetGPUDevice(int gpuDevice);
	bool g_IsDeviceEnabled();
	int g_getDeviceCount();

	void g_MatMul(int n_rowsA, int n_colsA, int n_colsB, int nzA, int nzB, int *rowsA, int *colsA, float *valuesA,
			int *rowsB, int *colsB, float *valuesB, int **row_res, int **cols_res, float **values_res, int& res_nz);
	void g_Confidence(float* values, float* diagonal, int elements, float* result);
	void g_StandardDeviation(float* mat, int rows, int cols, float* meanSD, float* result);
	void g_MeanSD(int rows, int cols, int depth, float *h_data, float *result, bool considerZeros);

	void g_Sum(float* values1, float* values2, int elements, float*result);
	void g_Subtract(float* values1, float* values2, int elements, float*result);
	void g_Binarize(float* values, int elements, float* result);
	void g_Transpose(float* values, int elements, float* result);
	void g_Invert(float* values, int elements, float* result);
	void g_Diagonalize(float* values, int vertices, float* result);
	void g_UpperDiagonal(float* values, int vertices, float* result);
	void g_LowerDiagonal(float* values, int vertices, float* result);
	void g_TransitiveClosure(float* values, int vertices, float* result);
}

void deleteMatrix(SpMatf *matrix){
	delete matrix;
	matrix = NULL;
}

void setNonZeroData(SpMatf* mat, int* rows, int* cols, float* values, int size) {
	std::vector<Tf> tripletList;
	for (int i = 0; i < size; i++) {
		tripletList.push_back(Tf(rows[i], cols[i], values[i]));
	}
	mat->setFromTriplets(tripletList.begin(), tripletList.end());
}

void matrixMult(SpMatf *mat1, SpMatf *mat2, SpMatf *result){
	int nonZeros_1 = mat1->nonZeros();
	int nonZeros_2 = mat2->nonZeros();

	int *rows_1 = (int*) malloc(sizeof(int) * nonZeros_1);
	int *cols_1 = (int*) malloc(sizeof(int) * nonZeros_1);
	int *rows_2 = (int*) malloc(sizeof(int) * nonZeros_2);
	int *cols_2 = (int*) malloc(sizeof(int) * nonZeros_2);
	float *values_1 = (float*) malloc(sizeof(float) * nonZeros_1);
	float *values_2 = (float*) malloc(sizeof(float) * nonZeros_2);

	int k = 0;
	for (int i = 0; i < mat1->outerSize(); ++i){
		for (SpMatf::InnerIterator it((*mat1), i); it; ++it){
			rows_1[k] = it.row();
			cols_1[k] = it.col();
			values_1[k] = it.value();
			k++;
		}
	}

	k = 0;
	for (int i = 0; i < mat2->outerSize(); ++i){
		for (SpMatf::InnerIterator it((*mat2), i); it; ++it){
			rows_2[k] = it.row();
			cols_2[k] = it.col();
			values_2[k] = it.value();
			k++;
		}
	}

	int *res_rows, *res_cols, res_nz;
	float *res_data;
	g_MatMul(mat1->rows(), mat1->cols(), mat2->cols(), nonZeros_1, nonZeros_2,
			rows_1, cols_1, values_1, rows_2, cols_2, values_2, &res_rows, &res_cols, &res_data, res_nz);
	setNonZeroData(result, res_rows, res_cols, res_data, res_nz);
	free(res_rows);
	free(res_cols);
	free(res_data);
	free(rows_1);
	free(cols_1);
	free(rows_2);
	free(cols_2);
	free(values_1);
	free(values_2);
}

void calculateConfidence(SpMatf *mat1, SpMatf *result){
	int nonZeros = mat1->nonZeros();

	int *rows = (int*) malloc(sizeof(int) * nonZeros);
	int *cols = (int*) malloc(sizeof(int) * nonZeros);
	float *values = (float*) malloc(sizeof(float) * nonZeros);
	float *diagonal = (float*) malloc(sizeof(float) * nonZeros);
	float *res_data = (float*) malloc(sizeof(float) * nonZeros);

	int k = 0;
	for (int i = 0; i < mat1->outerSize(); ++i){
		for (SpMatf::InnerIterator it((*mat1), i); it; ++it){
			rows[k] = it.row();
			cols[k] = it.col();
			values[k] = it.value();
			diagonal[k] = mat1->coeffRef(rows[k], rows[k]);
			k++;
		}
	}
	g_Confidence(values, diagonal, nonZeros, res_data);
	setNonZeroData(result, rows, cols, res_data, nonZeros);
	free(rows);
	free(cols);
	free(res_data);
	free(values);
	free(diagonal);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    resetGPU
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_resetGPU
  (JNIEnv *env, jclass obj, jint gpuDevice) {
	g_ResetAndSetGPUDevice(gpuDevice);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    isGPUEnabled
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_processor_MatrixProcessor_isGPUEnabled
  (JNIEnv *env, jclass obj) {
	return g_IsDeviceEnabled();
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    getDeviceCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_processor_MatrixProcessor_getDeviceCount
  (JNIEnv *env, jclass obj) {
	return g_getDeviceCount();
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    getSparseData
 * Signature: (J)[Lprocessor/Cell;
 */
JNIEXPORT jobjectArray JNICALL Java_processor_MatrixProcessor_getSparseData
  (JNIEnv *env, jclass obj, jlong pointer) {
	SpMatf* mat = (SpMatf*)pointer;

	std::vector<CellInfo> nonZeros;

	for (int i = 0; i < mat->outerSize(); ++i) {
		for (SpMatf::InnerIterator it((*mat), i); it; ++it) {
			CellInfo nz;
			nz.row = it.row();
			nz.col = it.col();
			nz.value = it.value();
			nonZeros.push_back(nz);
		}
	}

	jclass cell_class = env->FindClass("processor/Cell");
	jmethodID defConstructor = env->GetMethodID(cell_class, "<init>", "()V");
	jfieldID cell_row = env->GetFieldID(cell_class, "row", "I");
	jfieldID cell_col = env->GetFieldID(cell_class, "col", "I");
	jfieldID cell_value = env->GetFieldID(cell_class, "value", "F");

	jobjectArray jNonZeroArray = env->NewObjectArray(nonZeros.size(),
		cell_class, NULL);

	for (int i = 0; i < nonZeros.size(); i++) {

		jobject obj = env->NewObject(cell_class, defConstructor);

		env->SetIntField(obj, cell_row, nonZeros[i].row);
		env->SetIntField(obj, cell_col, nonZeros[i].col);
		env->SetFloatField(obj, cell_value, nonZeros[i].value);

		env->SetObjectArrayElement(jNonZeroArray, i, obj);
	}

	return jNonZeroArray;
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    getDenseData
 * Signature: (J[I[I)[Lprocessor/Cell;
 */
JNIEXPORT jobjectArray JNICALL Java_processor_MatrixProcessor_getDenseData
  (JNIEnv *env, jclass obj, jlong pointer, jintArray rows, jintArray cols) {
	float* valuesData = (float*)pointer;

	jsize cellSize = env->GetArrayLength(rows);
	jint* _jRowsData = env->GetIntArrayElements(rows, NULL);
	jint* _jColsData = env->GetIntArrayElements(cols, NULL);

	int* _rowsData = (int*)malloc(sizeof(int) * cellSize);
	int* _colsData = (int*)malloc(sizeof(int) * cellSize);

	for (int i = 0; i < cellSize; i++) {
		_rowsData[i] = _jRowsData[i];
	}

	for (int i = 0; i < cellSize; i++) {
		_colsData[i] = _jColsData[i];
	}

	std::vector<CellInfo> cells;

	for (int i=0; i < cellSize; i++) {
		CellInfo cell;
		cell.row = _rowsData[i];
		cell.col = _colsData[i];
		cell.value = valuesData[i];
		cells.push_back(cell);
	}

	jclass cell_class = env->FindClass("processor/Cell");
	jmethodID defConstructor = env->GetMethodID(cell_class, "<init>", "()V");
	jfieldID cell_row = env->GetFieldID(cell_class, "row", "I");
	jfieldID cell_col = env->GetFieldID(cell_class, "col", "I");
	jfieldID cell_value = env->GetFieldID(cell_class, "value", "F");

	jobjectArray jArray = env->NewObjectArray(cells.size(), cell_class, NULL);

	for (int i = 0; i < cells.size(); i++) {
		jobject obj = env->NewObject(cell_class, defConstructor);
		env->SetIntField(obj, cell_row, cells[i].row);
		env->SetIntField(obj, cell_col, cells[i].col);
		env->SetFloatField(obj, cell_value, cells[i].value);
		env->SetObjectArrayElement(jArray, i, obj);
	}
	return jArray;
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    createSparseMatrix
 * Signature: (IIZ)J
 */
JNIEXPORT jlong JNICALL Java_processor_MatrixProcessor_createSparseMatrix
  (JNIEnv *env, jclass obj, jint rows, jint cols) {
	SpMatf *mat = new SpMatf(rows, cols);
	return (long) mat;
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    createDenseMatrix
 * Signature: (IIZ)J
 */
JNIEXPORT jlong JNICALL Java_processor_MatrixProcessor_createDenseMatrix
  (JNIEnv *env, jclass obj, jint rows, jint cols) {
	float* v = (float*)malloc(sizeof(float) * rows*cols);
	return (long) v;
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    deleteSparseMatrix
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_processor_MatrixProcessor_deleteSparseMatrix
  (JNIEnv *env, jclass obj, jlong pointer) {
	SpMatf* _matrix = (SpMatf*) pointer;
	deleteMatrix(_matrix);
	return true;
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    deleteDenseMatrix
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_processor_MatrixProcessor_deleteDenseMatrix
  (JNIEnv *env, jclass obj, jlong pointer) {
	float* _matrix = (float*) pointer;
	delete _matrix;
	_matrix = NULL;
	return true;
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    setSparseData
 * Signature: (J[I[I[F)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_setSparseData
  (JNIEnv *env, jclass obj, jlong pointer, jintArray rows, jintArray cols, jfloatArray values) {
	SpMatf* mat = (SpMatf*)pointer;

	jsize rowsSize = env->GetArrayLength(rows);
	jsize colsSize = env->GetArrayLength(cols);
	jint* _jRowsData = env->GetIntArrayElements(rows, NULL);
	jint* _jColsData = env->GetIntArrayElements(cols, NULL);
	jfloat* _valuesData = env->GetFloatArrayElements(values, NULL);

	int* _rowsData = (int*)malloc(sizeof(int) * rowsSize);
	int* _colsData = (int*)malloc(sizeof(int) * colsSize);

	for (int i = 0; i < rowsSize; i++) {
		_rowsData[i] = _jRowsData[i];
	}

	for (int i = 0; i < colsSize; i++) {
		_colsData[i] = _jColsData[i];
	}

	setNonZeroData(mat, _rowsData, _colsData, _valuesData, rowsSize);

	env->ReleaseIntArrayElements(rows, _jRowsData, 0);
	env->ReleaseIntArrayElements(cols, _jColsData, 0);
	env->ReleaseFloatArrayElements(values, _valuesData, 0);
	env->DeleteLocalRef(rows);
	env->DeleteLocalRef(cols);
	env->DeleteLocalRef(values);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    setDenseData
 * Signature: (J[F)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_setDenseData
  (JNIEnv *env, jclass obj, jlong pointer, jfloatArray jvalues) {
	float* values = (float*) pointer;
	jsize nzSize = env->GetArrayLength(jvalues);
	jfloat* _valuesData = env->GetFloatArrayElements(jvalues, NULL);

	for (int i = 0; i < nzSize; i++) {
		values[i]=_valuesData[i];
	}

	env->ReleaseFloatArrayElements(jvalues, _valuesData, 0);
	env->DeleteLocalRef(jvalues);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    multiply
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_multiply
  (JNIEnv *env, jclass obj, jlong mat1, jlong mat2, jlong result) {
	SpMatf* _matrix1 = (SpMatf*) mat1;
	SpMatf* _matrix2 = (SpMatf*) mat2;
	SpMatf* _matResult = (SpMatf*) result;

	matrixMult(_matrix1, _matrix2, _matResult);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    confidence
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_confidence
  (JNIEnv *env, jclass obj, jlong pointer, jlong result) {
	SpMatf* _matrix = (SpMatf*) pointer;
	SpMatf* _res = (SpMatf*) result;

	calculateConfidence(_matrix, _res);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    getMin
 * Signature: (J)F
 */
JNIEXPORT jfloat JNICALL Java_processor_MatrixProcessor_getMin
  (JNIEnv *env, jclass obj, jlong pointer) {
	SpMatf* _matrix = (SpMatf*) pointer;
	float min = 0;
	for (int i = 0; i < _matrix->outerSize(); i++){
		for (SpMatf::InnerIterator it((*_matrix), i); it; ++it){
			if (it.value() < min)
				min = it.value();
		}
	}
	return min;
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    getMax
 * Signature: (J)F
 */
JNIEXPORT jfloat JNICALL Java_processor_MatrixProcessor_getMax
  (JNIEnv *env, jclass obj, jlong pointer) {
	SpMatf* _matrix = (SpMatf*) pointer;
	float max = 0;
	for (int i = 0; i < _matrix->outerSize(); i++){
		for (SpMatf::InnerIterator it((*_matrix), i); it; ++it){
			if (it.value() > max)
				max = it.value();
		}
	}
	return max;
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    sum
 * Signature: (JJIJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_sum
  (JNIEnv *env, jclass obj, jlong pointer1, jlong pointer2, jint elements, jlong jresult) {
	float* values1 = (float*) pointer1;
	float* values2 = (float*) pointer2;
	float* result = (float*) jresult;
	g_Sum(values1, values2, elements, result);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    subtract
 * Signature: (JJIJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_subtract
  (JNIEnv *env, jclass obj, jlong pointer1, jlong pointer2, jint elements, jlong jresult) {
	float* values1 = (float*) pointer1;
	float* values2 = (float*) pointer2;
	float* result = (float*) jresult;
	g_Subtract(values1, values2, elements, result);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    binarize
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_binarize
  (JNIEnv* env, jclass obj, jlong pointer, jlong result) {

	SpMatf* _mat = (SpMatf*)pointer;
	SpMatf* _res = (SpMatf*)result;

	int nonZeros = _mat->nonZeros();

	int* rows = (int*)malloc(sizeof(int) * nonZeros);
	int* cols = (int*)malloc(sizeof(int) * nonZeros);
	float* values = (float*)malloc(sizeof(float) * nonZeros);
	float* res_data = (float*)malloc(sizeof(float) * nonZeros);


	int k = 0;
	for (int i = 0; i < _mat->outerSize(); ++i) {
		for (SpMatf::InnerIterator it((*_mat), i); it; ++it) {
			rows[k] = it.row();
			cols[k] = it.col();
			values[k] = it.value();
			k++;
		}
	}

	g_Binarize(values, nonZeros, res_data);
	setNonZeroData(_res, rows, cols, res_data, nonZeros);

	free(rows);
	free(cols);
	free(res_data);
	free(values);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    transpose
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_transpose
  (JNIEnv* env, jclass obj, jlong pointer, jlong result) {

	SpMatf* _mat = (SpMatf*)pointer;
		SpMatf* _res = (SpMatf*)result;

		int nonZeros = _mat->nonZeros();

		int* rows = (int*)malloc(sizeof(int) * nonZeros);
		int* cols = (int*)malloc(sizeof(int) * nonZeros);
		float* values = (float*)malloc(sizeof(float) * nonZeros);
		float* res_data = (float*)malloc(sizeof(float) * nonZeros);


		int k = 0;
		for (int i = 0; i < _mat->outerSize(); ++i) {
			for (SpMatf::InnerIterator it((*_mat), i); it; ++it) {
				rows[k] = it.row();
				cols[k] = it.col();
				values[k] = it.value();
				k++;
			}
		}

		g_Transpose(values, nonZeros, res_data);
		setNonZeroData(_res, cols, rows, res_data, nonZeros);

		free(rows);
		free(cols);
		free(res_data);
		free(values);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    invert
 * Signature: (IJJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_invert
  (JNIEnv *env, jclass obj, jint elements, jlong pointer, jlong jresult) {
	float* values = (float*) pointer;
	float* result = (float*) jresult;
	g_Invert(values, elements, result);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    diagonalize
 * Signature: (IJJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_diagonalize
  (JNIEnv *env, jclass obj, jint vertices, jlong pointer, jlong jresult) {
	float* values = (float*) pointer;
	float* result = (float*) jresult;
	g_Diagonalize(values, vertices, result);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    upperDiagonal
 * Signature: (IIJJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_upperDiagonal
  (JNIEnv *env, jclass obj, jint vertices, jlong pointer, jlong jresult) {
	float* values = (float*) pointer;
	float* result = (float*) jresult;
	g_UpperDiagonal(values, vertices, result);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    lowerDiagonal
 * Signature: (IIJJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_lowerDiagonal
  (JNIEnv *env, jclass obj, jint vertices, jlong pointer, jlong jresult) {
	float* values = (float*) pointer;
	float* result = (float*) jresult;
	g_LowerDiagonal(values, vertices, result);
}

/*
 * Class:     processor_MatrixProcessor
 * Method:    transitiveClosure
 * Signature: (IJJ)V
 */
JNIEXPORT void JNICALL Java_processor_MatrixProcessor_transitiveClosure
  (JNIEnv *env, jclass obj, jint vertices, jlong pointer, jlong jresult) {
	float* values = (float*) pointer;
	float* result = (float*) jresult;
	g_TransitiveClosure(values, vertices, result);
}
