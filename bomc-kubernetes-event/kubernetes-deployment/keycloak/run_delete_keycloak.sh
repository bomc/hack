#!/bin/bash

echo Delete keycloak deployment. 

kubectl delete -f ./bomc-deployment.yml

sleep 5s # waits 5 seconds

echo Delete keycloak service

kubectl delete -f ./bomc-service.yml