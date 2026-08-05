#!/bin/bash
echo "====================================================="
echo "  GreenCycle - Compile and Run"
echo "  Requires Oracle JDK 8 (JavaFX bundled)"
echo "====================================================="
echo ""

mkdir -p out
find src -name "*.java" | sort > sources.txt

echo "Compiling all source files..."
javac -d out @sources.txt
EXIT_CODE=$?
rm -f sources.txt

if [ $EXIT_CODE -eq 0 ]; then
    echo ""
    echo " Compilation successful!"
    echo " Starting GreenCycle ..."
    echo ""
    echo " Demo accounts:"
    echo "   Admin : admin / admin123"
    echo "   Users : ahmad / pass123  |  siti / pass123"
    echo ""
    java -cp out Main
else
    echo ""
    echo " COMPILATION FAILED. Check the errors listed above."
fi
