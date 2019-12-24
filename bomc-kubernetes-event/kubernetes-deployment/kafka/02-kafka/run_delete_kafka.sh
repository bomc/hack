#!/bin/bash

echo Delete kafka deployment.

kubectl delete -f ./kafka-broker1-deployment.yml

sleep 5s # waits 5 seconds

echo Delete kafka service.

kubectl delete -f ./kafka-broker1-service.yml