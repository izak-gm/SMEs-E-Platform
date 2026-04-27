#!/bin/bash
set -e

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

print_success "Destroying k8s architecture ..."
# delete
kubectl delete -f k8s/00-namespace-configMap.yml