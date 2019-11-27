#!/bin/bash

echo Apply keycloak deployment. 

kubectl apply -f ./bomc-deployment.yml

sleep 5s # waits 5 seconds

echo Apply keycloak service

kubectl apply -f ./bomc-service.yml