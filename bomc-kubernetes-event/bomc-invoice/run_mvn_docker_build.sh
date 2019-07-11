#!/bin/bash
mvn clean install -PskipTest && docker build -t bomc/bomc-invoice:v1.0.0 .
