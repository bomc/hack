#!/bin/bash

echo Delete postgres deployment. 

kubectl delete -f ./bomc-deployment.yml

sleep 5s # waits 5 seconds

echo Delete postgres service

kubectl delete -f ./bomc-service.yml