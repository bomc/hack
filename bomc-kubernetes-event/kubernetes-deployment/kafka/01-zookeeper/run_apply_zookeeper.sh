#!/bin/bash

echo Apply zookeeper deployment.

kubectl apply -f ./02-zookeeper-deployment.yml

sleep 5s # waits 5 seconds

echo Apply zookeeper service.

kubectl apply -f ./01-zookeeper-service.yml