#!/bin/bash

echo Apply bomc-app namespace

kubectl apply -f ./bomc-app_namespace.yml

echo Apply postgres deployment. 

kubectl apply -f ./bomc-deployment.yml

sleep 5s # waits 5 seconds

echo Apply postgres service

kubectl apply -f ./bomc-service.yml