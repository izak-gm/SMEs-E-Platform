#!/bin/bash
set -e # exits if an error is found

echo "Deploying SMEs-platform microservice"

# create namespace
kubectl create namespace -f k8s/00-namespace-configMap.yml

# Deploy secrets

# Deploy ConfigMaps

# Deploy config-server

# Wait for the config-server to start and test if it was deployed successfully

# Deploy the rest of the services/containers