#!/bin/sh
packageName="SequenceAlignmentBenchmark"
srcDir="."
cpPath="."
dstDir="./"
binPath="$packageName/Efficient"
mkdir -p "$dstDir"
javac -cp "$cpPath" -d "$dstDir" "$srcDir/Basic.java"
javac -cp "$cpPath" -d "$dstDir" "$srcDir/SequenceAlignmentUtils.java"
javac -cp "$cpPath" -d "$dstDir" "$srcDir/Efficient.java"
java "$binPath" "$1" "$2"

