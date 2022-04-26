@echo off
set aquabasileaBaseDir="F:\Dominic\Documents\Eigene Dateien\Programmierung\Java only\aquabasilea\"
set aquabasileaUiBaseDir=%aquabasileaBaseDir%aquabasilea-kurs-bucher-web-ui\src\main\ui\
set webTargetPath=%aquabasileaBaseDir%aquabasilea-kurs-bucher-rest-app\src\main\resources\static\
set targetBuildOutput=%aquabasileaUiBaseDir%dist\

Rem actual build
cd %aquabasileaUiBaseDir%
call npm run build

Rem copy file back
if not exist %webTargetPath% mkdir %webTargetPath%
xcopy %targetBuildOutput% %webTargetPath% /y /s
RMDIR %targetBuildOutput% /S /Q