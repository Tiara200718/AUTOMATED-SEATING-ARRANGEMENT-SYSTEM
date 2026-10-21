@echo off
echo ===================================================
echo Starting Smart Exam Seating Arrangement System...
echo ===================================================

REM Compile all java source files
javac -cp "lib/*" -d bin src/com/examseating/**/*.java src/com/examseating/*.java

REM Run the Main Java Application
java -cp "bin;lib/*" com.examseating.Main

pause
