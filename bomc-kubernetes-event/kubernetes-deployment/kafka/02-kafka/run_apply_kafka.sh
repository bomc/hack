#!/bin/bash

echo Apply kafka service.

kubectl apply -f ./kafka-broker1-service.yml

sleep 5s # waits 5 seconds

echo Apply kafka deployment.

kubectl apply -f ./kafka-broker1-deployment.yml
