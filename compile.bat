@echo off
echo =====================================================
echo   GreenCycle - Compile and Run
echo   Requires Oracle JDK 8 (JavaFX bundled)
echo =====================================================
echo.

if not exist out mkdir out
dir /s /b src\*.java > sources.txt

echo Compiling all source files...
javac -encoding UTF-8 -d out @sources.txt


if %errorlevel% == 0 (
    del sources.txt
    echo.
    echo  Compilation successful!
    echo  Starting GreenCycle ...
    echo.
    echo  Demo accounts:
    echo    Admin : admin / admin123
    echo    Users : ahmad / pass123  ^|  siti / pass123  
    echo.
    java -cp out Main
) else (
    del sources.txt
    echo.
    echo  COMPILATION FAILED. Check the errors listed above.
    pause
)
