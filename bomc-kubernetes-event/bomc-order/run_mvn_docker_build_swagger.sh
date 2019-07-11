#!/bin/bash

# http://192.168.99.100:31380/bomc-order/swagger-ui/

mvn clean install -Pswagger && docker build -t bomc/bomc-order:v1.0.0 .
