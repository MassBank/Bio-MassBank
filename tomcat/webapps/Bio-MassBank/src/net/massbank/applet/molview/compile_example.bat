SET PATH=%PATH%;C:\Program Files\Java\jdk1.5.0_16\bin
SET opt=-d ./build -encoding UTF-8 -Xlint:none -cp ./build;

pushd build
del /q *.*
for /D %%f in ( * ) do rmdir /s /q "%%f"
popd

xcopy ..\armDraw .\build\ /E /H
javac %opt% ..\common\*.java
javac %opt% *.java

del MolView.jar
cd ./build
javac -cp .;./massbank/*.class ../MolView.java -d .
jar cfmv ../MolView.jar ../MANIFEST.MF *
pause
