@ECHO OFF

SET JAVA_HOME=C:\Progra~1\Java\jdk1.8.0_212
SET VC="C:\Progra~2\Microsoft Visual Studio\2019\Community\VC"
SET CUDA="V:\NVIDIA\CUDA\v10.1"
SET EIGEN="V:\cuda-libs\eigen"
SET CUSP="V:\cuda-libs\cusp"

CLS

javac -classpath .:../main/java ..\main\java\util\NativeUtils.java ..\main\java\processor\Cell.java ..\main\java\processor\MatrixProcessor.java
cd ..\main\java\
javah processor.MatrixProcessor
del processor\*.class
del util\*.class
move/Y *.h ..\..\cuda
cd ..\..\cuda

IF "%1" EQU "clean" GOTO CLEAN
GOTO COMPILE

:CLEAN
IF EXIST *.exp del *.exp
IF EXIST *.lib del *.lib
IF EXIST *.obj del *.obj
IF EXIST *.o del *.o
GOTO END

:COMPILE
IF "%VSCMD_ARG_HOST_ARCH%"=="" GOTO PREP
@ECHO.
ECHO ENVIRONMENT ALREADY PREPARED!
@ECHO.
GOTO BUILD

:PREP
@ECHO ON
call %VC%\Auxiliary\Build\vcvars64.bat
ECHO.

:BUILD
ECHO BEGINNING KERNEL COMPILATION...
@ECHO ON
nvcc -Xcompiler -I%CUDA%\include -I%CUDA%\samples\common -I%CUSP% -L%CUDA%\lib\x64 -lm -m64 -c -arch=sm_30 kernel.cu -o kernel.o
@ECHO.
@ECHO OFF
ECHO END OF KERNEL COMPILATION!
ECHO.
ECHO BEGINNING LIBRARY ASSEMBLY...
@ECHO ON
call cl /EHsc /I%JAVA_HOME%\include\win32 /I%JAVA_HOME%\include /I%EIGEN% /I%CUDA%\include /I%CUDA%\samples\common MatrixProcessor.cpp /D_USRDLL /D_WINDLL kernel.o %CUDA%\lib\x64\cudart.lib /link /DLL /OUT:MatrixProcessor.dll
@IF %ERRORLEVEL% EQU 0 GOTO SUCCESS 
@GOTO ERROR

:SUCCESS
@ECHO OFF
@ECHO.
IF EXIST ..\main\resources\lib\win_64\MatrixProcessor.dll del ..\main\resources\lib\win_64\MatrixProcessor.dll
move MatrixProcessor.dll ..\main\resources\lib\win_64\
@ECHO.
ECHO LIBRARY (DLL) ASSEMBLED!
@ECHO.
GOTO CLEAN

:ERROR
@ECHO.
@ECHO FAILED BUILDING/ASSEMBLING DLL!
@ECHO.

:END