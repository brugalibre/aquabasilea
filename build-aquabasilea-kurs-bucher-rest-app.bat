@echo off
Rem Aquabasilea konstants
set baseWorkspaceDir="F:\Dominic\Documents\Eigene Dateien\Programmierung\Java only"
set aquabasileaKursBucherJarName=aquabasilea-kurs-bucher-rest-app-1.0-SNAPSHOT.jar
set aquabasileaKursBuchenBaseDir="%baseWorkspaceDir%\aquabasilea\"
set aquabasileaKursBuchenBuildOutputDir=%aquabasileaKursBuchenBaseDir%aquabasilea-kurs-bucher-rest-app\build\libs\%aquabasileaKursBucherJarName%

Rem web-navigator konstants
set webNavigatorBaseDir="%baseWorkspaceDir%\zeiterfassung-web-bucher\web-navigator"
set GRADLE_OPTS=-Dfile.encoding=utf-8
set buildStartedAtPath=%cd%

cd %webNavigatorBaseDir%

echo "<====================================>"
echo "<====    build web-navigator     ====>"
echo "<====================================>"
call gradlew clean build shadowJar publishToMavenLocal

Rem build web resources
cd %buildStartedAtPath%
echo "<==========================================>"
echo "<====        aquabasilea-web-ui        ====>"
echo "<==========================================>"
call build-aquabasilea-kurs-bucher-web-ui.bat

Rem build aquabasilea-web-navigator
cd %aquabasileaKursBuchenBaseDir%
echo "<==========================================>"
echo "<====    aquabasilea-web-navigator     ====>"
echo "<==========================================>"
cd aquabasilea-web-navigator
call gradlew clean build shadowJar publishToMavenLocal
cd ..

Rem build aquabasilea-migros-api
cd %aquabasileaKursBuchenBaseDir%
echo "<==========================================>"
echo "<=====     aquabasilea-migros-api     =====>"
echo "<==========================================>"
cd aquabasilea-migros-api
call gradlew clean build shadowJar publishToMavenLocal
cd ..

Rem build aquabasilea-kurs-bucher
echo "<========================================>"
echo "<====    aquabasilea-kurs-bucher     ====>"
echo "<========================================>"
cd aquabasilea-kurs-bucher
call gradlew clean build shadowJar publishToMavenLocal
cd ..

Rem build aquabasilea-kurs-bucher-rest-app
echo "<============================================>"
echo "<====  aquabasilea-kurs-bucher-rest-app  ====>"
echo "<============================================>"
cd aquabasilea-kurs-bucher-rest-app
call gradlew clean build publishToMavenLocal
cd ..

Rem copy file back
cd %buildStartedAtPath%
cd..
xcopy "%aquabasileaKursBuchenBuildOutputDir%" /y /s
RMDIR %webTargetPath% /S /Q