#!/bin/bash
set -e

REGISTRY="localhost:5000"
# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "Current directory: $(pwd)"
echo -e "${GREEN}Registry: $REGISTRY"

# Spring boot services
SPRING_SERVICES=(
  "config-server:8888"
  "discovery-server:8761"
  "gateway-server:8222"
  "auth-service:8090"
  "notification-server:8014"
  "payment-server:8013"
)
# Django services
DJANGO_SERVICES=(
    "products-service:8012"
    "orders-server:8011"
)
# Function to build Spring boot Services
build_spring_service(){
  local service=$1
  local port=$2

  echo "Building $service with Maven for the Microservice"
  cd "services/$service" || return 1 # returning 1 means exit gracefully

  # Clean and package maven build
  # Skipping tests
  if ! mvn clean package -DskipTests;then
    echo "Maven build failed for $service"
    cd ..
    return 1
  fi
  # Check if the jar file is available and created
  if ! ls target/*.jar 1> /dev/null 2>&1;then
    echo "No jar found in target/ directory"
    cd ..
    return 1
  fi
  # Build the image got spring boot
  echo "Building and pushing the docker images to the local registry at $REGISTRY"
  echo "Docker file location $(pwd)"
  docker build -t "$REGISTRY/$service:latest" .
  docker push "$REGISTRY/$service:latest"

  cd ..
  echo "Built $service SpringBoot successfully"
}

# Django function to create and push images
build_django_service(){
  local service=$1
  local port=$2

  echo -e "${GREEN}Building $service for Django services"
  cd "django_microservices/$service"

  # Build docker image
  docker build -t "$REGISTRY/$service:latest" .
  docker push "$REGISTRY/$service:latest"

  cd ..
  echo "Built $service for Django service successfully"
}
# Build spring Services
for service_port in "${SPRING_SERVICES[@]}";do
  service="${service_port%:*}"
  port="${service_port#*:}"     # Gets "8888"
  build_spring_service "$service" "$port"
done

#Build Django services
for service_port in "${DJANGO_SERVICES[@]}";do
   service="${service_port%:*}"
   port="${service_port#*:}"
  build_django_service "$service" "$port"
done
echo "ALL MICROSERVICES BUILT AND PUSHED LOCALLY AT $REGISTRY SUCCESSFULLY"