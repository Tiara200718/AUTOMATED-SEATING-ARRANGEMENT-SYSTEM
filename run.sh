#!/bin/bash
echo "==================================================="
echo "Starting Smart Exam Seating Arrangement System..."
echo "==================================================="

# Create bin if not exists
mkdir -p bin

# Compile all java source files
javac -cp "lib/*" -d bin $(find src -name "*.java")

# Run the Main Java Application
java -cp "bin:lib/*" com.examseating.Main
