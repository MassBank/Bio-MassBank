SET PATH=%PATH%;C:\Program Files (x86)\Java\jdk1.5.0_16\bin
SET CLASSPATH=".;C:\Program Files (x86)\Java\jre1.5.0_16\lib\plugin.jar;D:\xampp\tomcat\webapps\Bio-MassBank\WEB-INF\lib\jsonic-1.3.0.jar;"
SET MASSBANK_NET=D:\xampp\tomcat\webapps\Bio-MassBank\src\net\massbank\
SET opt=-d ./build -encoding UTF-8 -Xlint:none -cp ./build;%CLASSPATH%

rem pushd build
rem del /q *.*
rem for /D %%f in ( * ) do rmdir /s /q "%%f"
rem popd

rem xcopy ..\jsonic-1.2.6\net .\build\net\ /E /H
rem xcopy ..\armDraw .\build\ /E /H
javac %opt% "%MASSBANK_NET%applet\common\*.java"
javac %opt% "%MASSBANK_NET%core\common\*.java"
javac %opt% "%MASSBANK_NET%core\get\instrument\GetInstrumentInvoker.java"
javac %opt% "%MASSBANK_NET%core\get\instrument\GetInstrumentResults.java"
javac %opt% "%MASSBANK_NET%core\get\record\GetRecordTitleInvoker.java"
javac %opt% "%MASSBANK_NET%core\get\record\GetRecordInfoInvoker.java"
javac %opt% "%MASSBANK_NET%tools\search\SearchParameter.java"
javac %opt% "%MASSBANK_NET%tools\search\spectrum\SpectrumSearchInvoker.java"
javac %opt% "%MASSBANK_NET%tools\search\spectrum\SpectrumSearchParameter.java"
javac %opt% "%MASSBANK_NET%tools\search\spectrum\SpectrumSearchResult.java"
rem javac %opt% ../common/*.java
javac %opt% *.java

cd .\build
jar cfmv ..\FSearchApplet.jar ..\MANIFEST.MF *
pause
