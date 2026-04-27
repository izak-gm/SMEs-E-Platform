#!/bin/bash

set -e # exits if an error is found

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
  echo -e "${BLUE}[INFO]${NC} $1"
}
print_success(){
  echo -e "${GREEN} [SUCCESS]${NC} $1"
}
print_error() {
  echo -e "${RED}[ERROR]${NC} $1"
}
print_warning() {
  echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_success "Deploying SMEs-platform microservice"

# Ensure namespace exists
# create namespace and configMaps
kubectl create namespace microservices --dry-run=client -o yaml | kubectl apply -f k8s/00-namespace-configMap.yml

print_success "$(pwd)"
# Deploy secrets
kubectl apply -f k8s/secrets.yml

# Deploy Config Server
print_success "Deploying Config Server..."
kubectl apply -f k8s/01-config-server.yml

# Wait for the config-server to start and test if it was deployed successfully
print_success " Waiting for Config Server..."
#kubectl wait --for=condition=ready -n microservices pod -l app=config-server --timeout=120s
kubectl get pods -n microservices -l app=config-server

# Test Config Server
print_success "Config Server is ready!"
#kubectl exec -it deployment/config-server -n microservices -- \
#  curl -s http://localhost:8888/actuator/health

# Deploy discovery-server, gateway-service
kubectl apply -f k8s/02-discovery-server.yml
kubectl apply -f k8s/03-gateway-service.yml


# Deploy the rest of the services/containers
kubectl apply -f k8s/100-auth-service.yml

# Wait for Auth Server
print_success " Waiting for Auth Server..."
#kubectl wait --for=condition=ready pod -l app=auth-service -n microservices --timeout=120s
kubectl get pods -n microservices -l app=auth-service

# Django projects
kubectl apply -f k8s/200-store-product-service.yml
kubectl apply -f k8s/201-order-service.yml

# Spring Boot services
kubectl apply -f k8s/101-payment-service.yml
kubectl apply -f k8s/102-notification-service.yml

# Show status
print_success ""
print_success " Deployment Status:"
kubectl get pods -n microservices -l app=config-server
kubectl get pods -n microservices -l app=discovery-server
kubectl get pods -n microservices -l app=gateway-service
kubectl get pods -n microservices -l app=products-service
kubectl get pods -n microservices -l app=orders-service
kubectl get pods -n microservices -l app=payment-service
kubectl get pods -n microservices -l app=notification-service

#kubectl get svc -n microservices -l 'app in (config-server,auth-service)'

print_success ""
print_success "Access Services:"
print_success "  Config Server: kubectl port-forward -n microservices svc/config-server 8888:8888"
print_success "  Auth Server:   kubectl port-forward -n microservices svc/auth-service 8090:8090"
print_success ""
print_success "Test endpoints:"
print_success "  curl http://localhost:8888/auth-service/default"
print_success "  curl http://localhost:8090/actuator/health"
print_success "  curl http://localhost:8011/actuator/health"
print_success "  curl http://localhost:8012/actuator/health"
print_success "  curl http://localhost:8013/actuator/health"
print_success "  curl http://localhost:8014/actuator/health"