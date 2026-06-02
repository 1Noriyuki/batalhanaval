@echo off
cd /d "%~dp0"

echo Compiling project with Maven...

mvn -q compile

if %ERRORLEVEL% equ 0 (
    echo.
    echo Compilation successful!
) else (
    echo.
    echo Compilation failed! Check errors above.
    pause
)
