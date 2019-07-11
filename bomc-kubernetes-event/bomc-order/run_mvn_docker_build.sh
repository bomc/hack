#!/bin/bash
mvn clean install -PskipTest && docker build -t bomc/bomc-order:v1.0.0 .
