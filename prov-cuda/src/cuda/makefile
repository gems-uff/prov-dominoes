CUDA := /usr/local/cuda
EIGEN := /usr/local/eigen
CUSP := /usr/local/cusp
JAVAJNI := /System/Library/Frameworks/JavaVM.framework/Versions/A/Headers
LIBS := .

UNAME := $(shell uname -s)
LIBNAME := 

ifeq ($(UNAME),Linux)
   OUT_DIR := ../main/resources/lib/linux_64
   LIBNAME := MatrixProcessor.so
endif
	
ifeq ($(UNAME),Darwin) 
   OUT_DIR := ../main/resources/lib/osx_64
   LIBNAME := MatrixProcessor.jnilib
endif

MKDIR_P = mkdir -p
   
.PHONY: directories all clean

directories: $(OUT_DIR)

$(OUT_DIR):
	@echo "Teste"
	@echo $(OUT_DIR)
	$(MKDIR_P) $(OUT_DIR)

ifndef CUDA
   $(error CUDA is not set)
endif

ifndef EIGEN
   $(error EIGEN is not set)
endif

ifndef CUSP
   $(error CUSP is not set)
endif

ifndef JAVAJNI
   $(error JAVAJNI is not set)
endif


all : directories | MatrixProcessor.jnilib
	
processor_MatrixProcessor.h : 
	javac ../main/java/processor/Cell.java ../main/java/processor/MatrixProcessor.java 
	javah -verbose -jni -classpath .:../main/java processor.MatrixProcessor
	mv ../main/java/processor/processor_MatrixProcessor.h .
	
MatrixProcessor.o : processor_MatrixProcessor.h

   ifeq ($(UNAME),Linux)
	g++ -fPIC -c -I $(JAVAJNI) -I $(JAVAJNI)/linux -I $(EIGEN) \
	    -c MatrixProcessor.c -o MatrixProcessor.o -m64 
   endif

   ifeq ($(UNAME),Darwin)
	   g++ -I $(JAVAJNI) -I $(EIGEN) \
	      -c MatrixProcessor.c -o MatrixProcessor.o -m64 -fPIC 
   endif
	 
kernel.o : 
   ifeq ($(UNAME),Linux)
	$(CUDA)/bin/nvcc -Xcompiler -fpic -I$(CUDA)/include -I$(CUDA)/samples/common -L$(CUDA)/lib/x64 -L$(CUSP) -lm -m64 -c -arch=sm_30 kernel.cu -o kernel.o 
   endif

   ifeq ($(UNAME),Darwin)
	$(CUDA)/bin/nvcc --cudart=static -Xcompiler -static -ccbin  /usr/bin/clang -Xcompiler -fpic -I$(CUDA)/include -I$(CUDA)/samples/common -L$(CUDA)/lib/x64 -lm -m64 -c -arch=sm_30 kernel.cu -o kernel.o 
   endif
		
MatrixProcessor.jnilib : MatrixProcessor.o kernel.o
   ifeq ($(UNAME),Darwin)
	g++ -shared MatrixProcessor.o \
     -Wl,-rpath,$(CUDA)/lib \
	 kernel.o -o $(OUT_DIR)/$(LIBNAME) -m64 -L $(CUDA)/lib -lcudart_static 
   endif
   
   ifeq ($(UNAME),Linux)
	g++ -shared MatrixProcessor.o \
	 -Wl,-rpath,$(CUDA)/lib64  \
	 kernel.o -o $(OUT_DIR)/$(LIBNAME) -m64 -L $(CUDA)/lib64 -lcudart 
   endif
	
clean : 
	-rm processor_MatrixProcessor.h
	-rm $(OUT_DIR)/$(LIBNAME)
	-rm MatrixProcessor.o
	-rm kernel.o
