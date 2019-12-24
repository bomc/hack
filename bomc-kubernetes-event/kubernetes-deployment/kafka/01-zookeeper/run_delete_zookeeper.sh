#!/bin/bash

echo Delete zookeeper deployment.

kubectl delete -f ./02-zookeeper-deployment.yml

sleep 5s # waits 5 seconds

echo Delete zookeeper service.

kubectl delete -f ./01-zookeeper-service.yml