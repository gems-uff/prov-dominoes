#include <thrust/host_vector.h>
#include <thrust/device_vector.h>
#include <thrust/generate.h>
#include <thrust/sort.h>
#include <thrust/copy.h>
#include <thrust/reduce.h>
#include <algorithm>
#include <cstdlib>
#include <cuda.h>
#include <cuda_runtime.h>

#include <cusp/complex.h>
#include <cusp/coo_matrix.h>
#include <cusp/multiply.h>
#include <cusp/print.h>

#include <helper_cuda.h>
#include <stdio.h>
#include <math.h>

#define N_THREADS_X 16
#define N_THREADS_Y 16

__global__ void K_CheckNonZerosInCol(float *raw, int rows, int cols, int *nonZeros){
	int idx = blockDim.x * blockIdx.x + threadIdx.x;
	if (idx < rows){
		for (int i = 0; i < cols; i++){
			if (raw[idx * cols + i] > 0){
				nonZeros[idx] = 1;
			}
		}
	}
}

__global__ void K_Mean(float* mat_sum_depths, float *out_mean, int rows, int cols, float divide_by){
	float sum_depths  = 0;
	int columIdx = blockIdx.x * blockDim.x + threadIdx.x;
	if (columIdx < cols){
		for (int i = 0; i < rows; i++){
			sum_depths += mat_sum_depths[columIdx + (i * cols)];
		}
		out_mean[columIdx] = sum_depths / divide_by ;
	}
}

__global__ void K_Variance(float* layer, float* mean, int rows, int cols){
	int idx = blockIdx.x * blockDim.x + threadIdx.x;
	int idy = blockIdx.y * blockDim.y + threadIdx.y;
	if ((idx < cols) && (idy < rows)){
		float meanv = mean[idx];
		float value = meanv - layer[idy * cols + idx];
		layer[idy * cols + idx] = value * value;
	}
}

__global__ void K_StandardDeviation(float* mat_sum_depths, float *out_sd, int rows, int cols, int depths){
	float sum_depths  = 0;
	int columIdx = blockIdx.x * blockDim.x + threadIdx.x;
	if (columIdx < cols){
		for (int i = 0; i < rows; i++){
			sum_depths += mat_sum_depths[columIdx + (i * cols)];
		}
		out_sd[columIdx] = sqrt(sum_depths / (rows  * depths));
	}
}

__global__ void AddKernel(float* _mat1, float *_mat2, float *_res, 
	int rows1, int cols, int cols2){
    int idX = blockDim.x * blockIdx.x + threadIdx.x;
    int idY = blockDim.y * blockIdx.y + threadIdx.y;
    
    int id = idY * cols2 + idX;
    if (id < rows1 * cols2){
    	_res[id] = 0;
    	int mat1_row = idY * cols;
    	for ( int i = 0; i < cols; i++){
    		int mat2_col = i * cols2 + idX;
    		_res[id] += _mat1[mat1_row + i] * _mat2[mat2_col];
    	}
    }
}

__global__ void StandardScoreKernel(float* _mat, int rows, int cols,
	float* meanSD, float *res){
    int idx = blockDim.x * blockIdx.x + threadIdx.x;
    if (idx < cols){
    	for (int i = 0; i < rows; i++){
    		int idxElement = i * cols + idx;    		    						
			float _mean = meanSD[idx];
			float _sd = meanSD[cols + idx];
			res[idxElement] = (_mat[idxElement] - _mean) / _sd;
		}
    }
}

__global__ void ConfidenceKernel(float *values, float *diagonal, int elements, float *result){
    int idx = blockDim.x * blockIdx.x + threadIdx.x;
    if (idx < elements){
    	if (diagonal[idx] > 0)
    		result[idx] = values[idx] / diagonal[idx];
    }
}

// INICIO DOS PROV-KERNELS...

__global__ void binarizeKernel(float* values, int elements, float* result) {
	int idx = blockDim.x * blockIdx.x + threadIdx.x;
	if (idx < elements) {
		if (values[idx] > 0) {
			result[idx] = 1;
		} else {
			result[idx] = 0;
		}
	}
}

__global__ void invertKernel(float* values, int elements, float* result) {
	int idx = blockDim.x * blockIdx.x + threadIdx.x;
	if (idx < elements) {
		if (values[idx] > 0) {
			result[idx] = 0;
		} else {
			result[idx] = 1;
		}
	}
}

__global__ void diagonalizeKernel(float* values, int v, float* result) {
	int i = blockDim.x * blockIdx.x + threadIdx.x;
	int j = blockDim.y * blockIdx.y + threadIdx.y;
	if (i < v && j < v) {
		if (i==j) {
			result[i*v + j] = values[i*v + j];
		} else {
			result[i*v + j] = 0;
		}
	}
}

__global__ void upperDiagonalKernel(float* values, int v, float* result) {
	int i = blockDim.x * blockIdx.x + threadIdx.x;
	int j = blockDim.y * blockIdx.y + threadIdx.y;
	if (i < v && j < v) {
		if (j >= i) {
			result[i*v + j] = values[i*v + j];
		} else {
			result[i*v + j] = 0;
		}
	}
}

__global__ void lowerDiagonalKernel(float* values, int v, float* result) {
	int i = blockDim.x * blockIdx.x + threadIdx.x;
	int j = blockDim.y * blockIdx.y + threadIdx.y;
	if (i < v && j < v) {
		if (i >= j) {
			result[i*v + j] = values[i*v + j];
		} else {
			result[i*v + j] = 0;
		}
	}
}

__global__ void prepareClosureKernel(float* values, int v, float* result) {
	int i = blockDim.x * blockIdx.x + threadIdx.x;
	int j = blockDim.y * blockIdx.y + threadIdx.y;
	if (i < v && j < v) {
		result[i*v + j] = values[i*v + j] > 0 ? 1 : 0;
		if (i == j) {
			result[i*v + j] = 1;
		}
	}
}

__global__ void transitiveClosureKernel(float* values, int k, int v, float* result) {
	int i = blockDim.x * blockIdx.x + threadIdx.x;
	int j = blockDim.y * blockIdx.y + threadIdx.y;
	if (i < v) {
		if (j < v) {
			if (((result[i*v + k] != 0) && (result[k*v + j] != 0))) {
				if (i != j) { // ignorar próprio nó (i=j).
					float distIK = (i == k ? 0 : result[i*v + k]);
					float distKJ = (k == j ? 0 : result[k*v + j]);
					if (result[i*v + j] == 0) { // caso em que não foi calculado result entre IJ ainda.
						result[i*v + j] = distIK + distKJ;							
					} else if (distIK + distKJ < result[i*v + j]){ // atualizar se novos result forem menores que o atual.
						result[i*v + j] = distIK + distKJ;
					}
				}
			}
		}
	}
}

__global__ void rasterizeClosureKernel(float* matrix, int v) {
	int i = blockDim.x * blockIdx.x + threadIdx.x;
	int j = blockDim.y * blockIdx.y + threadIdx.y;
	if (i < v && j < v) {
		if (matrix[i*v + j] > 0) {
			matrix[i*v + j] = 1 / matrix[i*v + j];
		}
	}
}

// FIM DOS PROV-KERNELS!

extern "C" {

	void g_ResetAndSetGPUDevice(int gpuDevice) {
		checkCudaErrors(cudaSetDevice(gpuDevice));
		checkCudaErrors (cudaDeviceReset());checkCudaErrors
		(cudaSetDevice(gpuDevice));
	}
	
	int g_getDeviceCount() {
		int nDevices = 0;
		if (cudaGetDeviceCount(&nDevices) != cudaSuccess) {
			cudaGetLastError();
			nDevices = 0;
		}
		return nDevices;
	}
	
	bool g_IsDeviceEnabled() {
		return g_getDeviceCount()>0;
	}
	
	void g_StandardDeviation(float* mat, int rows, int cols, 
		float* meanSD, float* result){
		
		float *d_mat;
		float *d_meanSD;
		float *d_result;
		
		checkCudaErrors(cudaMalloc(&d_mat, sizeof(float) * rows * cols));
		checkCudaErrors(cudaMemcpy(d_mat, mat, sizeof(float) * rows * cols, cudaMemcpyHostToDevice));
		
		checkCudaErrors(cudaMalloc(&d_meanSD, sizeof(float) * 2 * cols));
		checkCudaErrors(cudaMemcpy(d_meanSD, meanSD, sizeof(float) * 2 * cols, cudaMemcpyHostToDevice));
		
		checkCudaErrors(cudaMalloc(&d_result, sizeof(float) * rows * cols));
		
		dim3 blockDim(N_THREADS_X * N_THREADS_Y, 1, 1);
		dim3 gridDim(ceil((float)cols/(N_THREADS_X *  N_THREADS_Y)), 1, 1);
		
		StandardScoreKernel<<<gridDim, blockDim>>>(d_mat, rows, cols, d_meanSD, d_result);
		
		checkCudaErrors(cudaMemcpy(result, d_result, sizeof(float) * rows * cols, cudaMemcpyDeviceToHost));
		checkCudaErrors(cudaFree(d_mat));
		checkCudaErrors(cudaFree(d_meanSD));
		checkCudaErrors(cudaFree(d_result));
	}
	
	
	void g_MeanSD(int rows, int cols, int depth, float *h_data, float *result, bool considerZeros){
		float *h_layer_keys;
		checkCudaErrors(cudaMalloc(&h_layer_keys, sizeof(float) * rows * cols));
		
		for (int i = 0; i < rows; i++){
			
			for (int j = 0; j < cols; j++){
				h_layer_keys[i * cols + j] = j;
			}
		}

		float *d_raw, *d_val_res, *d_mean_sd, *d_sum_depths;
		int *d_kraw, *d_keys_res, *d_nonZeros;
		checkCudaErrors(cudaMalloc((void**) &d_raw, sizeof(float) * rows * cols));
		checkCudaErrors(cudaMalloc((void**) &d_kraw, sizeof(int) * rows * cols));
		checkCudaErrors(cudaMalloc((void**) &d_val_res, sizeof(float) * rows * cols));	
		checkCudaErrors(cudaMalloc((void**) &d_keys_res, sizeof(int) * rows * cols));
		checkCudaErrors(cudaMalloc((void**) &d_mean_sd, sizeof(float) * cols * 2));
		checkCudaErrors(cudaMalloc((void**) &d_sum_depths, sizeof(float) * depth * cols));
		checkCudaErrors(cudaMemset(d_val_res, 0, sizeof(float) * rows * cols));
		checkCudaErrors(cudaMemset(d_keys_res, 0, sizeof(int) * rows * cols));
		
		if (!considerZeros){
			checkCudaErrors(cudaMalloc((void**) &d_nonZeros, sizeof(int) * rows));
			checkCudaErrors(cudaMemset(d_nonZeros, 0, sizeof(int) * rows));
		}

		thrust::device_ptr<float> dev_ptr(d_raw);
		thrust::device_ptr<int> dev_ptr_k(d_kraw);
		thrust::device_ptr<int> dev_ptr_k_res(d_keys_res);
		thrust::device_ptr<float> dev_ptr_v_res(d_val_res);

		for (int i = 0; i < depth; i++){
			checkCudaErrors(cudaMemcpy(d_raw, &h_data[i * rows * cols], 
					sizeof(float) * rows * cols, cudaMemcpyHostToDevice));
			checkCudaErrors(cudaMemcpy(d_kraw, h_layer_keys, 
					sizeof(int) * rows * cols, cudaMemcpyHostToDevice));
			
			if (!considerZeros){
				dim3 blockDim_nz(256, 1, 1);
				dim3 gridDim_nz( ceil((float)rows/256), 1, 1);
				K_CheckNonZerosInCol<<<gridDim_nz, blockDim_nz>>>(d_raw, rows, cols, d_nonZeros);
			}
					
			thrust::sort_by_key(dev_ptr_k, dev_ptr_k + (rows * cols), dev_ptr);
			checkCudaErrors(cudaDeviceSynchronize());
			thrust::reduce_by_key(dev_ptr_k, dev_ptr_k+(rows * cols), dev_ptr, dev_ptr_k_res, dev_ptr_v_res);
			checkCudaErrors(cudaDeviceSynchronize());
			checkCudaErrors(cudaMemcpy(&d_sum_depths[i * cols], d_val_res, sizeof(float) * cols, cudaMemcpyDeviceToDevice));
		}
		
		float *_result;
		checkCudaErrors(cudaMalloc(&_result, sizeof(float) * cols * depth));
		checkCudaErrors(cudaMemcpy(_result, d_mean_sd, sizeof(float) * cols * depth, cudaMemcpyDeviceToHost));

		// Calculate the mean
		int divide_by = rows * depth;
		
		if (!considerZeros){
			thrust::device_ptr<int> dev_ptr_nonZeros(d_nonZeros);
			divide_by = thrust::reduce(dev_ptr_nonZeros, dev_ptr_nonZeros + rows) * depth;
		}
		checkCudaErrors(cudaDeviceSynchronize());
		
		dim3 blockDim_m(256, 1, 1);
		dim3 gridDim_m( ceil((float)cols/256), 1, 1);
		K_Mean<<<gridDim_m, blockDim_m>>>(d_sum_depths, d_mean_sd, rows, cols, (float)divide_by);
		checkCudaErrors(cudaDeviceSynchronize());
		
		checkCudaErrors(cudaMemcpy(result, d_mean_sd, sizeof(float) * cols * 2, cudaMemcpyDeviceToHost));

		if (!considerZeros){
			cudaFree(d_nonZeros);
		}
		cudaFree(d_sum_depths);
		cudaFree(d_mean_sd);   	
		cudaFree(d_raw);
		cudaFree(d_kraw);
		cudaFree(d_val_res);
		cudaFree(d_keys_res);
		cudaFree(_result);
		cudaFree(h_layer_keys);
	}
	
	void g_Confidence(float* values, float* diagonal, int elements, float* result){
		float *d_values;
		float *d_diagonal;
		float *d_result;
		
		checkCudaErrors(cudaMalloc(&d_values, sizeof(float) * elements));
		checkCudaErrors(cudaMemcpy(d_values, values, sizeof(float) * elements, cudaMemcpyHostToDevice));
		
		checkCudaErrors(cudaMalloc(&d_diagonal, sizeof(float) * elements));
		checkCudaErrors(cudaMemcpy(d_diagonal, diagonal, sizeof(float) * elements, cudaMemcpyHostToDevice));
		
		checkCudaErrors(cudaMalloc(&d_result, sizeof(float) * elements));
		checkCudaErrors(cudaMemset(d_result, 0, sizeof(float) * elements));
		
		dim3 blockDim(N_THREADS_X * N_THREADS_Y, 1, 1);
		dim3 gridDim(ceil((float) elements/(N_THREADS_X *  N_THREADS_Y)), 1, 1);
		
		ConfidenceKernel<<<gridDim, blockDim>>>(d_values, d_diagonal, elements, d_result);
		
		checkCudaErrors(cudaMemcpy(result, d_result, sizeof(float) * elements, cudaMemcpyDeviceToHost));
		checkCudaErrors(cudaFree(d_values));
		checkCudaErrors(cudaFree(d_diagonal));
		checkCudaErrors(cudaFree(d_result));
	}
		
	
	void g_Binarize(float* values, int elements, float* result) {
		float* d_values;
		float* d_result;
		checkCudaErrors(cudaMalloc(&d_values, sizeof(float) * elements));
		checkCudaErrors(
				cudaMemcpy(d_values, values, sizeof(float) * elements,
						cudaMemcpyHostToDevice));
	
		checkCudaErrors(cudaMalloc(&d_result, sizeof(float) * elements));
		checkCudaErrors(cudaMemset(d_result, 0, sizeof(float) * elements));
	
		dim3 blockDim(N_THREADS_X * N_THREADS_Y, 1);
		dim3 gridDim(ceil((float) elements / (N_THREADS_X * N_THREADS_X)), 1, 1);
	
		binarizeKernel<<<gridDim, blockDim>>>(d_values, elements, d_result);
	
		checkCudaErrors(
				cudaMemcpy(result, d_result, sizeof(float) * elements,
						cudaMemcpyDeviceToHost));
	
		checkCudaErrors(cudaFree(d_values));
		checkCudaErrors(cudaFree(d_result));
	}
	
	void g_Invert(float* values, int elements, float* result) {
		float* d_values;
		float* d_result;
		checkCudaErrors(cudaMalloc(&d_values, sizeof(float) * elements));
		checkCudaErrors(
				cudaMemcpy(d_values, values, sizeof(float) * elements,
						cudaMemcpyHostToDevice));
	
		checkCudaErrors(cudaMalloc(&d_result, sizeof(float) * elements));
		checkCudaErrors(cudaMemset(d_result, 0, sizeof(float) * elements));
	
		dim3 blockDim(N_THREADS_X * N_THREADS_Y, 1);
		dim3 gridDim(ceil((float) elements / (N_THREADS_X * N_THREADS_X)), 1, 1);
	
		invertKernel<<<gridDim, blockDim>>>(d_values, elements, d_result);
	
		checkCudaErrors(
				cudaMemcpy(result, d_result, sizeof(float) * elements,
						cudaMemcpyDeviceToHost));
	
		checkCudaErrors(cudaFree(d_values));
		checkCudaErrors(cudaFree(d_result));
	}
	
	void g_Diagonalize(float* values, int v, float* result) {
		float* d_values;
		float* d_result;
		checkCudaErrors(cudaMalloc(&d_values, sizeof(float) * v*v));
		checkCudaErrors(
				cudaMemcpy(d_values, values, sizeof(float) * v*v,
						cudaMemcpyHostToDevice));
	
		checkCudaErrors(cudaMalloc(&d_result, sizeof(float) * v*v));
		checkCudaErrors(cudaMemset(d_result, 0, sizeof(float) * v*v));
	
		dim3 blockDim(N_THREADS_X , N_THREADS_Y, 1);
		dim3 gridDim(ceil((float) v / (N_THREADS_X)), ceil((float) v / (N_THREADS_Y)), 1);
	
		diagonalizeKernel<<<gridDim, blockDim>>>(d_values, v, d_result);
	
		checkCudaErrors(
				cudaMemcpy(result, d_result, sizeof(float) * v*v,
						cudaMemcpyDeviceToHost));
	
		checkCudaErrors(cudaFree(d_values));
		checkCudaErrors(cudaFree(d_result));
	}
	
	void g_UpperDiagonal(float* values, int v, float* result) {
		float* d_values;
		float* d_result;
		checkCudaErrors(cudaMalloc(&d_values, sizeof(float) * v*v));
		checkCudaErrors(
				cudaMemcpy(d_values, values, sizeof(float) * v*v,
						cudaMemcpyHostToDevice));
	
		checkCudaErrors(cudaMalloc(&d_result, sizeof(float) * v*v));
		checkCudaErrors(cudaMemset(d_result, 0, sizeof(float) * v*v));
	
		dim3 blockDim(N_THREADS_X , N_THREADS_Y, 1);
		dim3 gridDim(ceil((float) v / (N_THREADS_X)), ceil((float) v / (N_THREADS_Y)), 1);
	
		upperDiagonalKernel<<<gridDim, blockDim>>>(d_values, v, d_result);
	
		checkCudaErrors(
				cudaMemcpy(result, d_result, sizeof(float) * v*v,
						cudaMemcpyDeviceToHost));
	
		checkCudaErrors(cudaFree(d_values));
		checkCudaErrors(cudaFree(d_result));
		}
	
	void g_LowerDiagonal(float* values, int v, float* result) {
			float* d_values;
			float* d_result;
			checkCudaErrors(cudaMalloc(&d_values, sizeof(float) * v*v));
			checkCudaErrors(
					cudaMemcpy(d_values, values, sizeof(float) * v*v,
							cudaMemcpyHostToDevice));
		
			checkCudaErrors(cudaMalloc(&d_result, sizeof(float) * v*v));
			checkCudaErrors(cudaMemset(d_result, 0, sizeof(float) * v*v));
		
			dim3 blockDim(N_THREADS_X , N_THREADS_Y, 1);
			dim3 gridDim(ceil((float) v / (N_THREADS_X)), ceil((float) v / (N_THREADS_Y)), 1);
		
			lowerDiagonalKernel<<<gridDim, blockDim>>>(d_values, v, d_result);
		
			checkCudaErrors(
					cudaMemcpy(result, d_result, sizeof(float) * v*v,
							cudaMemcpyDeviceToHost));
		
			checkCudaErrors(cudaFree(d_values));
			checkCudaErrors(cudaFree(d_result));
		}
	
	void g_TransitiveClosure(float* values, int v, float* result) {
		float* d_values;
		float* d_result;
		checkCudaErrors(cudaMalloc(&d_values, sizeof(float) * v*v));
		checkCudaErrors(
				cudaMemcpy(d_values, values, sizeof(float) * v*v,
						cudaMemcpyHostToDevice));
	
		checkCudaErrors(cudaMalloc(&d_result, sizeof(float) * v*v));
		checkCudaErrors(cudaMemset(d_result, 0, sizeof(float) * v*v));
	
		
		dim3 blockDim(N_THREADS_X, N_THREADS_Y, 1);
		dim3 gridDim(ceil((float) v / (N_THREADS_X)), ceil((float) v / (N_THREADS_Y)), 1);
		
		prepareClosureKernel<<<gridDim, blockDim>>>(d_values, v, d_result);
		
		for (int k=0; k < v; k++) {
			transitiveClosureKernel<<<gridDim, blockDim>>>(d_values, k, v, d_result);
		}
		
		rasterizeClosureKernel<<<gridDim, blockDim>>>(d_result, v);
	
		checkCudaErrors(
				cudaMemcpy(result, d_result, sizeof(float) * v*v,
						cudaMemcpyDeviceToHost));
	
		checkCudaErrors(cudaFree(d_values));
		checkCudaErrors(cudaFree(d_result));
	}
	
	void g_MatMul(int n_rowsA, int n_colsA, int n_colsB, int nzA, int nzB,
		int *rowsA, int *colsA, float *valuesA,
		int *rowsB, int *colsB, float *valuesB,
		int **row_res, int **col_res, float **value_res,
		int& res_nz){

		
		cusp::coo_matrix<int,float,cusp::host_memory> matA(n_rowsA,n_colsA,nzA);
		for (int i = 0; i < nzA; i++){
			matA.row_indices[i] = rowsA[i]; matA.column_indices[i] = colsA[i]; matA.values[i] = valuesA[i];
		}
		cusp::coo_matrix<int,float,cusp::device_memory> matA_d = matA;
		
		cusp::coo_matrix<int,float,cusp::host_memory> matB(n_colsA,n_colsB,nzB);
		for (int i = 0; i < nzB; i++){
			matB.row_indices[i] = rowsB[i]; matB.column_indices[i] = colsB[i]; matB.values[i] = valuesB[i];
		}
		cusp::coo_matrix<int,float,cusp::device_memory> matB_d = matB;
		
		cusp::coo_matrix<int,float,cusp::device_memory> matRes_d(n_rowsA,n_colsB, n_rowsA * n_colsB);
		
		cusp::multiply(matA_d, matB_d, matRes_d);
		
		cusp::coo_matrix<int,float,cusp::host_memory> matRes = matRes_d;
		
		res_nz = matRes.num_entries;
		int *_row_res = new int[res_nz];
		int *_col_res = new int[res_nz];
		float *_value_res = new float[res_nz];
		
		for(size_t n = 0; n < res_nz; n++)
		{
			_row_res[n] = matRes.row_indices[n];
			_col_res[n] = matRes.column_indices[n];
			_value_res[n] = matRes.values[n];
		}
		
		*row_res = _row_res;
		*col_res = _col_res;
		*value_res = _value_res;
	}
}
