#!/bin/bash
mvn clean install && docker build -t bomc/${artifactId}:v1.0.0 .
