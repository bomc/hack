#!/bin/sh
mvn clean package && docker build -t bomc/ping:v1.0.0 .
