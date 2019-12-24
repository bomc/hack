#!/bin/bash

echo Apply kafka-manager service.

kubectl apply -f ./kafka-manager-service.yml

sleep 5s # waits 5 seconds

echo Apply kafka-manager deployment.

kubectl apply -f ./kafka-manager-deployment.yml
