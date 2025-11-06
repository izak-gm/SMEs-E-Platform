#!/bin/bash
set -e

echo "===================================="
echo "|| =====     ==      ||   //        "
echo "||    //    // \\    ||  //         " 
echo "||   //    //   \\   || //          "
echo "||  //    // === \\  || \\          "
echo "|| ====  //       \\ ||  \\         "
echo "===================================="
# ============================================
# Run All Backend Services for SMEs E-Platform
# ============================================

# Base directories
BASE_DIR=$(pwd)
# echo"Running on the base directory $BASE_DIR"
SERVICES_DIR="$BASE_DIR/services"
DJANGO_DIR="$BASE_DIR/django_microservices"

# Services paths
CONFIG_SERVER="$SERVICES_DIR/config-server"
DISCOVERY_SERVER="$SERVICES_DIR/discovery-server"
GATEWAY_SERVER="$SERVICES_DIR/gateway"
AUTH_SERVICE="$SERVICES_DIR/auth-service"

# Django microservices
DJANGO_PRODUCTS_SERVICE="$DJANGO_DIR/products_service"
DJANGO_ORDERS_SERVICE="$DJANGO_DIR/orders_service"

# --------------------------------------------
# Define Service Ports
# --------------------------------------------
CONFIG_PORT=8888
DISCOVERY_PORT=8761
GATEWAY_PORT=8222
AUTH_PORT=8090
PRODUCTS_PORT=8012
ORDERS_PORT=8011


# Python virtual environments
GLOBAL_VENV="$BASE_DIR/.venv"  # shared virtual environment

# Function to wait for a port to open
wait_for_port() {
  local port=$1
  local name=$2
  echo "⏳ Waiting for $name on port $port..."
  while ! nc -z localhost $port; do
    sleep 2
  done
  echo "✅ $name is up on port $port!"
}

echo "============================================"
echo "🚀 Starting SMEs E-Platform Backend Services"
echo "============================================"

# --------------------------------------------
# 1️⃣ Start Config Server
# --------------------------------------------
echo "🚀 Starting Config Server on port $CONFIG_PORT ..."
cd "$CONFIG_SERVER"
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=$CONFIG_PORT" &
CONFIG_PID=$!
wait_for_port 8888 "Config Server"
cd "$BASE_DIR"

# --------------------------------------------
# 2️⃣ Start Discovery Server
# --------------------------------------------
echo "🚀 Starting Discovery Server on port $DISCOVERY_PORT..."
cd "$DISCOVERY_SERVER"
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=$DISCOVERY_PORT" &
DISCOVERY_PID=$!
wait_for_port $DISCOVERY_PORT "Discovery Server"
cd "$BASE_DIR"

# --------------------------------------------
# 3️⃣ Start Gateway Service
# --------------------------------------------
echo "🚀 Starting Gateway Service on port $GATEWAY_PORT..."
cd "$GATEWAY_SERVER"
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=$GATEWAY_PORT" &
GATEWAY_PID=$!
wait_for_port $GATEWAY_PORT "Gateway Service"
cd "$BASE_DIR"

# --------------------------------------------
# 4️⃣ Start Auth Service
# --------------------------------------------
echo "🚀 Starting Auth Service on port $AUTH_PORT..."
cd "$AUTH_SERVICE"

# Export secret key
export secret_key=ZKmeapmW/5fgkRI16seeGMtCeWU7B4XcE8ZavPvTHOozkWh+YFzr9AdWlS4iS1vU
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=$AUTH_PORT" &
AUTH_PID=$!
wait_for_port $AUTH_PORT "Auth Service"
cd "$BASE_DIR"

# --------------------------------------------
# 5️⃣ Start Django Services
# --------------------------------------------

echo "🔍 DEBUG: DJANGO_PRODUCTS_SERVICE=$DJANGO_PRODUCTS_SERVICE"
echo "🔍 DEBUG: DJANGO_ORDERS_SERVICE=$DJANGO_ORDERS_SERVICE"

Current=$(pwd)
echo $Current

echo "🚀 Activating virtual environment..."
source "$GLOBAL_VENV/bin/activate"

REQUIREMENTS_FILE="$DJANGO_DIR/requirements.txt"
REQUIREMENTS_HASH_FILE="$GLOBAL_VENV/.requirements_hash"

if [ -f "$REQUIREMENTS_FILE" ]; then
  new_hash=$(sha256sum "$REQUIREMENTS_FILE")
  if [ "$new_hash" != "$(cat $REQUIREMENTS_HASH_FILE 2>/dev/null)" ]; then
    echo "📦 New dependencies detected — installing..."
    pip install -r "$REQUIREMENTS_FILE"
    echo "$new_hash" > "$REQUIREMENTS_HASH_FILE"
  else
    echo "✅ Dependencies already up-to-date."
  fi
fi

echo "🚀 Starting Products Django Service on port $PRODUCTS_PORT..."
cd "$DJANGO_PRODUCTS_SERVICE"
python manage.py runserver $PRODUCTS_PORT &
PRODUCTS_PID=$! & python manage.py run_consumer
wait_for_port $PRODUCTS_PORT "Products Django Service"

echo "🚀 Starting Orders Django Service on port $ORDERS_PORT..."
cd "$DJANGO_ORDERS_SERVICE"
python manage.py runserver $ORDERS_PORT &
ORDERS_PID=$!
wait_for_port $ORDERS_PORT "Orders Django Service"

deactivate
cd "$BASE_DIR"

# --------------------------------------------
# ✅ Summary
# --------------------------------------------
echo "============================================"
echo "✅ All Services Started Successfully!"
echo "--------------------------------------------"
echo "Config Server:     PID=$CONFIG_PID  | Port=$CONFIG_PORT"
echo "Discovery Server:  PID=$DISCOVERY_PID | Port=$DISCOVERY_PORT"
echo "Gateway:           PID=$GATEWAY_PID   | Port=$GATEWAY_PORT"
echo "Auth Service:      PID=$AUTH_PID      | Port=$AUTH_PORT"
echo "Products Service:  PID=$PRODUCTS_PID  | Port=$PRODUCTS_PORT"
echo "Orders Service:    PID=$ORDERS_PID    | Port=$ORDERS_PORT"
echo "--------------------------------------------"
echo "🧠 Use Ctrl + C to stop (or run stop_all_services.sh)"
echo "============================================"

# Keep the script alive to maintain subprocesses
wait
