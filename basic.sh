#!/bin/sh
packageName="SequenceAlignmentBenchmark"
srcDir="."
cpPath="."
dstDir="./"
binPath="$packageName/Basic"
mkdir -p "$dstDir"
javac -cp "$cpPath" -d "$dstDir" "$srcDir/SequenceAlignmentUtils.java"
javac -cp "$cpPath" -d "$dstDir" "$srcDir/Basic.java"
java "$binPath" "$1" "$2"

