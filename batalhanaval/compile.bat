@echo off
cd /d "c:\Users\Enzo\Documents\estudo programacao\batalhanaval"

echo Compiling all Java files...

REM Create output directory
if not exist out mkdir out

REM Compile with Java 11+ (required for text blocks)
javac -d out ui\Main.java domain\model\*.java domain\player\*.java service\*.java repository\*.java config\*.java

if %ERRORLEVEL% equ 0 (
    echo.
    echo Compilation successful!
    echo.
    echo Running game...
    java -cp "out;lib\*" com.batalhanaval.ui.Main
) else (
    echo.
    echo Compilation failed! Check errors above.
    pause
)