@echo off
Rem Aquabasilea konstants
set aquabasileaKursBucherJarName=aquabasilea-kurs-bucher-1.0-SNAPSHOT-all.jar
set aquabasileaWebNavigatorKursBucherJarName=aquabasilea-web-kurs-bucher-1.0-SNAPSHOT.jar
set aquabasileaKursBuchenBaseDir=C:\Users\std\Documents\programmierung\IdeaProjects\zeiterfassung-browser\

set aquabasileaKursBuchenDir=%aquabasileaKursBuchenBaseDir%aquabasilea-kurs-bucher\
set aquabasileaKursBuchenBuildOutput=%aquabasileaKursBuchenDir%build\libs\%aquabasileaKursBucherJarName%
set aquabasileaKursBuchenWebNavigatorLibDir=%aquabasileaKursBuchenBaseDir%aquabasilea-kurs-bucher\libs

Rem Basic webnavigator konstants
set webNavigatorJarName=zeiterfassung-web-bucher-1.0-SNAPSHOT-all.jar
set webNavigatorDir=%aquabasileaKursBuchenBaseDir%zeiterfassung-browser-bucher\
set webNavigatorDirBuildOutput=%webNavigatorDir%build\libs\%webNavigatorJarName%

set initialPath=%cd%

Rem build web-navigator
cd "%webNavigatorDir%
set GRADLE_OPTS=-Dfile.encoding=utf-8
call gradlew clean build shadowJar -x test

Rem copy file to aquabasilea-kurs-bucher
if not exist "%aquabasileaKursBuchenWebNavigatorLibDir%" mkdir %aquabasileaKursBuchenWebNavigatorLibDir%
xcopy "%webNavigatorDirBuildOutput%" "%aquabasileaKursBuchenWebNavigatorLibDir%" /S /Y
ren %aquabasileaKursBuchenWebNavigatorLibDir%\%webNavigatorJarName% %aquabasileaWebNavigatorKursBucherJarName%
RMDIR %aquabasileaKursBuchenWebNavigatorLibDir%\%webNavigatorJarName% /S /Q

Rem actual build
cd "%aquabasileaKursBuchenDir%"
call gradlew clean build shadowJar -x test

Rem copy file back
cd %initialPath%
cd..
xcopy "%aquabasileaKursBuchenBuildOutput%" /Y

pause