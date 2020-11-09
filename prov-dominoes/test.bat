@ECHO OFF

FOR /L  %%i IN (1,1,5) DO (
call prov-dominoes.bat eval:%1
rename performance-results-%1.txt performance-results-%1%%i.txt
)
