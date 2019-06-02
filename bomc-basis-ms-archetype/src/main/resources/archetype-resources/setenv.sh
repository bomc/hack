#!/bin/bash

echo "Set environment for java and maven." 

# Set java environment
export PATH=$PATH:"/C/Program Files/Java/jdk1.8.0_172/bin"

# Set maven environment
export PATH=$PATH:"/D/Tools/maven-3.5.4/bin"

# or set maven with a symbolic link for the build directory
#ln -s "/D/Tools/maven-3.5.4/bin" ./mvnbin
#export PATH=$PATH:/D/trunk/projects/bomc-kafka/mvnbin

echo $PATH