@ECHO OFF
@SETLOCAL
CLS

IF "%1" EQU "eval:cpu" GOTO EVALCPU
IF "%1" EQU "eval:gpu" GOTO EVALGPU

java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar
GOTO END

:EVALCPU
SET start=%time%
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-1\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,3%

ECHO RUNNING CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-cpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=CPU --script=samples\twitter\subcollection-1\script2-user-types-mentions.eps >> performance-results-cpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-cpu.txt
ECHO. >> performance-results-cpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-2\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,3%

ECHO RUNNING CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-cpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=CPU --script=samples\twitter\subcollection-2\script2-user-types-mentions.eps >> performance-results-cpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-cpu.txt
ECHO. >> performance-results-cpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-3\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,3%

ECHO RUNNING CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-cpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=CPU --script=samples\twitter\subcollection-3\script2-user-types-mentions.eps >> performance-results-cpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-cpu.txt
ECHO. >> performance-results-cpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-4\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,3%

ECHO RUNNING CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-cpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=CPU --script=samples\twitter\subcollection-4\script2-user-types-mentions.eps >> performance-results-cpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-cpu.txt
ECHO. >> performance-results-cpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-5\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,4%

ECHO RUNNING CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-cpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=CPU --script=samples\twitter\subcollection-5\script2-user-types-mentions.eps >> performance-results-cpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-cpu.txt
ECHO. >> performance-results-cpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-6\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,4%

ECHO RUNNING CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-cpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=CPU --script=samples\twitter\subcollection-6\script2-user-types-mentions.eps >> performance-results-cpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-cpu.txt
ECHO. >> performance-results-cpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-7\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,4%

ECHO RUNNING CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-cpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=CPU --script=samples\twitter\subcollection-7\script2-user-types-mentions.eps >> performance-results-cpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-cpu.txt
ECHO. >> performance-results-cpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~58,4%

ECHO RUNNING CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== CPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-cpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=CPU --script=samples\twitter\script2-user-types-mentions.eps >> performance-results-cpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-cpu.txt
ECHO. >> performance-results-cpu.txt
ECHO.
GOTO TIMECALC

:EVALGPU
SET start=%time%
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-1\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,3%

ECHO RUNNING GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-gpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=GPU --script=samples\twitter\subcollection-1\script2-user-types-mentions.eps >> performance-results-gpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-gpu.txt
ECHO. >> performance-results-gpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-2\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,3%

ECHO RUNNING GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-gpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=GPU --script=samples\twitter\subcollection-2\script2-user-types-mentions.eps >> performance-results-gpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-gpu.txt
ECHO. >> performance-results-gpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-3\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,3%

ECHO RUNNING GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-gpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=GPU --script=samples\twitter\subcollection-3\script2-user-types-mentions.eps >> performance-results-gpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-gpu.txt
ECHO. >> performance-results-gpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-4\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,3%

ECHO RUNNING GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-gpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=GPU --script=samples\twitter\subcollection-4\script2-user-types-mentions.eps >> performance-results-gpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-gpu.txt
ECHO. >> performance-results-gpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-5\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,4%

ECHO RUNNING GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-gpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=GPU --script=samples\twitter\subcollection-5\script2-user-types-mentions.eps >> performance-results-gpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-gpu.txt
ECHO. >> performance-results-gpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-6\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,4%

ECHO RUNNING GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-gpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=GPU --script=samples\twitter\subcollection-6\script2-user-types-mentions.eps >> performance-results-gpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-gpu.txt
ECHO. >> performance-results-gpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\subcollection-7\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~74,4%

ECHO RUNNING GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-gpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=GPU --script=samples\twitter\subcollection-7\script2-user-types-mentions.eps >> performance-results-gpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-gpu.txt
ECHO. >> performance-results-gpu.txt
ECHO.

FOR /F "delims=" %%i IN ('find/C "entity(" samples\twitter\twitter-governadores_en.provn') DO SET RESFIND=%%i
SET TWEETS=%RESFIND:~58,4%

ECHO RUNNING GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION...
ECHO ====== GPU PERFORMANCE ASSESSMENT FOR %TWEETS% TWEETS COLLECTION ====== >> performance-results-gpu.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.7.jar --mode=GPU --script=samples\twitter\script2-user-types-mentions.eps >> performance-results-gpu.txt
ECHO ====== END OF RESULTS ====== >> performance-results-gpu.txt
ECHO.
GOTO TIMECALC

:TIMECALC
SET end=%time%
SET options="tokens=1-4 delims=:.,"
FOR /f %options% %%a in ("%start%") do SET start_h=%%a&SET /a start_m=100%%b %% 100&SET /a start_s=100%%c %% 100&SET /a start_ms=100%%d %% 100
FOR /f %options% %%a in ("%end%") do SET end_h=%%a&SET /a end_m=100%%b %% 100&SET /a end_s=100%%c %% 100&SET /a end_ms=100%%d %% 100

SET /a hours=%end_h%-%start_h%
SET /a mins=%end_m%-%start_m%
SET /a secs=%end_s%-%start_s%
SET /a ms=%end_ms%-%start_ms%
IF %ms% lss 0 SET /a secs = %secs% - 1 & SET /a ms = 100%ms%
IF %secs% lss 0 SET /a mins = %mins% - 1 & SET /a secs = 60%secs%
IF %mins% lss 0 SET /a hours = %hours% - 1 & SET /a mins = 60%mins%
IF %hours% lss 0 SET /a hours = 24%hours%
IF 1%ms% lss 100 SET ms=0%ms%

SET /a totalsecs = %hours%*3600 + %mins%*60 + %secs%
ECHO OVERALL TIME EXECUTION: %hours%:%mins%:%secs%.%ms% (%totalsecs%.%ms%s total)
IF "%1" EQU "eval:cpu" GOTO ENDCPU
IF "%1" EQU "eval:gpu" GOTO ENDGPU

:ENDCPU
ECHO. >> performance-results-cpu.txt
ECHO OVERALL TIME EXECUTION: %hours%:%mins%:%secs%.%ms% (%totalsecs%.%ms%s total) >> performance-results-cpu.txt
GOTO END

:ENDGPU
ECHO. >> performance-results-gpu.txt
ECHO OVERALL TIME EXECUTION: %hours%:%mins%:%secs%.%ms% (%totalsecs%.%ms%s total) >> performance-results-gpu.txt
GOTO END

:END