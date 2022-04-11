@echo off
Rem Aquabasilea konstants
set aquabasileaKursBucherJarName=aquabasilea-kurs-bucher-app-1.0-SNAPSHOT.jar
set aquabasileaKursBuchenBaseDir=C:\Users\std\Documents\programmierung\IdeaProjects\aquabasilea\
set aquabasileaKursBuchenBuildOutputDir=%aquabasileaKursBuchenBaseDir%aquabasilea-kurs-bucher-app\build\libs\%aquabasileaKursBucherJarName%

Rem web-navigator konstants
set webNavigatorBaseDir=C:\Users\std\Documents\programmierung\IdeaProjects\zeiterfassung-browser\web-navigator

set buildStartedAtPath=%cd%

cd %webNavigatorBaseDir%

echo "<====================================>"
echo "<====    build web-navigator     ====>"
echo "<====================================>"
call gradlew clean build shadowJar publishToMavenLocal

cd %aquabasileaKursBuchenBaseDir%
Rem build aquabasilea-web-navigator
echo "<==========================================>"
echo "<====    aquabasilea-web-navigator     ====>"
echo "<==========================================>"
cd aquabasilea-web-navigator
call gradlew clean build shadowJar publishToMavenLocal
cd ..

Rem build aquabasilea-kurs-bucher
echo "<========================================>"
echo "<====    aquabasilea-kurs-bucher     ====>"
echo "<========================================>"
cd aquabasilea-kurs-bucher
call gradlew clean build -x Test shadowJar publishToMavenLocal
cd ..

Rem build ui-library
echo "<====================================>"
echo "<====         ui-library         ====>"
echo "<====================================>"
cd ui-library
call gradlew clean build shadowJar publishToMavenLocal
cd ..

Rem build aquabasilea-kurs-bucher-ui
echo "<============================================>"
echo "<====    aquabasilea-kurs-bucher-app     ====>"
echo "<============================================>"
cd aquabasilea-kurs-bucher-app
call gradlew clean build shadowJar publishToMavenLocal
cd ..

Rem copy file back
cd %buildStartedAtPath%
cd..
xcopy "%aquabasileaKursBuchenBuildOutputDir%" /Y