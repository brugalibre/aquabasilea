@echo off
Rem Aquabasilea konstants
set baseWorkspaceDir="C:\Users\domin\programmierung"
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

cd %buildStartedAtPath%
echo "<==========================================>"
echo "<====        aquabasilea-web-ui        ====>"
echo "<==========================================>"
call build-aquabasilea-kurs-bucher-web-ui.bat

cd "%aquabasileaKursBuchenBaseDir%aquabasilea-web-navigator"
echo "<==========================================>"
echo "<====    aquabasilea-web-navigator     ====>"
echo "<==========================================>"
call gradlew clean publishToMavenLocal
cd ..

cd "%aquabasileaKursBuchenBaseDir%aquabasilea-migros-api"
echo "<==========================================>"
echo "<=====     aquabasilea-migros-api     =====>"
echo "<==========================================>"
call gradlew clean publishToMavenLocal
cd ..

echo "<========================================>"
echo "<====    aquabasilea-kurs-bucher     ====>"
echo "<========================================>"
cd aquabasilea-kurs-bucher
call gradlew clean publishToMavenLocal
cd ..

echo "<============================================>"
echo "<====  aquabasilea-kurs-bucher-rest-app  ====>"
echo "<============================================>"
cd aquabasilea-kurs-bucher-rest-app
call gradlew clean publishToMavenLocal
cd ..

Rem copy file back
cd %buildStartedAtPath%
cd..
xcopy "%aquabasileaKursBuchenBuildOutputDir%" /y /s
RMDIR %webTargetPath% /S /Q