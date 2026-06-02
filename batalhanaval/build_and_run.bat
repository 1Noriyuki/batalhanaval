@echo off
cd /d "%~dp0"

echo Building and running project with Maven...

mvn -q compile exec:java -Dexec.mainClass=com.batalhanaval.ui.Main

if %ERRORLEVEL% neq 0 (
    echo.
    echo Execution failed! Check errors above.
    pause
)
