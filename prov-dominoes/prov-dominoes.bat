@ECHO OFF

IF "%1" EQU "performance" GOTO PERFORMANCE

java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.5.jar
GOTO END

:PERFORMANCE
ECHO ====== BEGINING OF CPU PERFORMANCE RESULTS ====== >> performance-results.txt
ECHO. >> performance-results.txt
ECHO. >> performance-results.txt
java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.5.jar --mode=CPU --script=samples\twitter\100-subcollection\script2-user-types-mentions.ces >> performance-results.txt
:: java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.5.jar --mode=CPU --script=samples\twitter\200-subcollection\script2-user-types-mentions.ces >> performance-results.txt
:: java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.5.jar --mode=CPU samples\twitter\300-subcollection\script2-user-types-mentions.ces >> performance-results.txt
:: java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.5.jar --mode=CPU samples\twitter\500-subcollection\script2-user-types-mentions.ces >> performance-results.txt
:: java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.5.jar --mode=CPU samples\twitter\1000-subcollection\script2-user-types-mentions.ces >> performance-results.txt
:: java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.5.jar --mode=CPU samples\twitter\2000-subcollection\script2-user-types-mentions.ces >> performance-results.txt
:: java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.5.jar --mode=CPU samples\twitter\3000-subcollection\script2-user-types-mentions.ces >> performance-results.txt
:: java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-v1.5.jar --mode=CPU samples\twitter\script2-user-types-mentions.ces >> performance-results.txt
ECHO. >> performance-results.txt
ECHO. >> performance-results.txt
echo ====== BEGINING OF CPU PERFORMANCE RESULTS ====== >> performance-results.txt
:END