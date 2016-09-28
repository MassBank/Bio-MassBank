SET PATH=%PATH%;C:\Program Files\Java\jdk1.5.0_16\bin
SET CLASSPATH=.;D:\xampp\tomcat\webapps\\Bio-MassBank\WEB-INF\lib\jsonic-1.2.6.jar;
SET CORE=D:\xampp\tomcat\webapps\\Bio-MassBank\src\net\massbank\core\
SET opt=-d ./build -encoding UTF-8 -Xlint:none -cp ./build;%CLASSPATH%

pushd build
del /q *.*
for /D %%f in ( * ) do rmdir /s /q "%%f"
popd

xcopy ..\jsonic-1.2.6\net .\build\net\ /E /H
xcopy ..\armDraw .\build\ /E /H
javac %opt% %CORE%common\*.java
javac %opt% %CORE%get\record\GetRecordInfoInvoker.java
javac %opt% "..\..\tools\search\SearchParameter.java"
javac %opt% "..\..\tools\search\peak\PeakSearchParameter.java"
javac %opt% ..\common\*.java
javac %opt% *.java

del DisplayApplet.jar
cd .\build
jar cfmv ..\DisplayApplet.jar ..\MANIFEST.MF *.class *
pause
