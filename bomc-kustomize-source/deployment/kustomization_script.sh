#!/bin/bash

echo _________________________________________________
echo Set up kustomization.yaml
echo -------------------------------------------------

echo Add basis deployment-config
kustomize edit add resource ./base/010-dc.yaml

echo Add basis service-config
kustomize edit add resource ./base/020-svc.yaml

echo Add application.properties
kustomize edit add configmap ping-cm --from-file=./prod/application.properties

echo Add env variable for prod profile
kustomize edit add patch ./prod/030-patch-env-dc.yaml

echo Add application-prod.properties
kustomize edit add configmap ping-cm --from-file=./prod/application-prod.properties

echo Set resources in production environment to prod- labels so that they can querried them by label selector.
kustomize edit set nameprefix 'prod-'

echo Add patch for memory limit
kustomize edit add patch ./prod/020-patch-memorylimit_dc.yaml

echo Add patch for healthcheck
kustomize edit add patch ./prod/010-patch-healthcheck_dc.yaml