#!/usr/bin/env bash

set -e    # Exits immediately if a command exits with a non-zero status.

echo run command: './run_util.sh xxx' where xxx is 'keycloak-logs, hrm-logs'

if [ "$1" == "keycloak-logs" ]; then

	POD_NAME=$(kubectl get pods -l name=keycloak -n bomc-app -o go-template --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}')
	kubectl logs -n bomc-app -f ${POD_NAME} keycloak

elif [ "$1" == "hrm-logs" ]; then

	POD_NAME=$(kubectl get pods -l name=bomc-hrm -n bomc-app -o go-template --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}')
	kubectl logs -n bomc-app -f ${POD_NAME} bomc-hrm

fi
