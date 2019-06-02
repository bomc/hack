#!/bin/bash
mvn clean install && docker build -t bomc/bomc-order:v1.0.0 .
