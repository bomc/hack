#!/bin/sh
mvn clean package -DskipTest && docker build -t 172.30.1.1:5000/bomc/ping:v1.0.0 .
