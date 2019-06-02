#!/bin/bash
mvn clean install && docker build -t bomc/bomc-controller:v1.0.0 .
