#!/bin/bash
set -e

# Colors for logs
GREEN="\e[32m"
BLUE="\e[34m"
YELLOW="\e[33m"
RESET="\e[0m"

echo "===================================="
echo "|| =====     ==      ||   //        "
echo "||    //    // \\    ||  //         " 
echo "||   //    //   \\   || //          "
echo "||  //    // === \\  || \\          "
echo "|| ====  //       \\ ||  \\         "
echo "===================================="

echo "============================================"
echo "🚀 Starting SMEs E-Platform Backend Services"
echo "============================================"


# Base directories
BASE_DIR=$(pwd)
SERVICES_DIR="$BASE_DIR/services"
DJANGO_DIR="$BASE_DIR/django_microservices"

# Spring Boot services
CONFIG_SERVER="$SERVICES_DIR/config-server"
DISCOVERY_SERVER="$SERVICES_DIR/discovery-server"
GATEWAY_SERVER="$SERVICES_DIR/gateway"
AUTH_SERVICE="$SERVICES_DIR/auth-service"

# Django microservices
DJANGO_PRODUCTS_SERVICE="$DJANGO_DIR/products_service"
DJANGO_ORDERS_SERVICE="$DJANGO_DIR/orders_service"

# Ports
CONFIG_PORT=8888
DISCOVERY_PORT=8761
GATEWAY_PORT=8222
AUTH_PORT=8090
PRODUCTS_PORT=8012
ORDERS_PORT=8011

# Python virtual environment
GLOBAL_VENV="$BASE_DIR/.venv"

# Function to wait for a port to open
wait_for_port() {
  local port=$1
  local name=$2
  echo "⏳ Waiting for $name to start on port $port..."
  while ! nc -z localhost $port; do
    sleep 2
  done
  echo "✅ $name is running on port $port!"
}
log() {
  echo -e "${GREEN}[INFO]${RESET} $1"
}

run_with_prefix() {
  prefix=$1
  shift
  "$@" 2>&1 | sed "s/^/[${prefix}] /" &
  echo $!
}

# Trap to stop all services cleanly
trap "log '🛑 Stopping all services...'; \
kill $CONFIG_PID $DISCOVERY_PID $GATEWAY_PID $AUTH_PID \
$PRODUCTS_PID $ORDERS_PID $CONSUMER_PID 2>/dev/null; exit 0" SIGINT SIGTERM

# 1️⃣ Start Spring Boot Services
echo "🚀 Starting Config Server on port $CONFIG_PORT ..."
cd "$CONFIG_SERVER"
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=$CONFIG_PORT" &
CONFIG_PID=$!
wait_for_port $CONFIG_PORT "Config Server"
cd "$BASE_DIR"


echo "🚀 Starting Discovery Server on port $DISCOVERY_PORT..."
cd "$DISCOVERY_SERVER"
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=$DISCOVERY_PORT" &
DISCOVERY_PID=$!
wait_for_port $DISCOVERY_PORT "Discovery Server"

# --------------------------------------------
# 3️⃣ Start Gateway Service
# --------------------------------------------
echo "🚀 Starting Gateway Service on port $GATEWAY_PORT..."
cd "$GATEWAY_SERVER"
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=$GATEWAY_PORT" &
GATEWAY_PID=$!
wait_for_port $GATEWAY_PORT "Gateway Service"

echo "🚀 Starting Auth Service on port $AUTH_PORT..."
cd "$AUTH_SERVICE"
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

echo "🌍 Activating Python environment..."
source "$GLOBAL_VENV/bin/activate"

# Dependency optimization
REQUIREMENTS_FILE="$DJANGO_DIR/requirements.txt"
REQUIREMENTS_HASH_FILE="$GLOBAL_VENV/.requirements_hash"

if [ -f "$REQUIREMENTS_FILE" ]; then
  new_hash=$(sha256sum "$REQUIREMENTS_FILE")
  if [ "$new_hash" != "$(cat $REQUIREMENTS_HASH_FILE 2>/dev/null)" ]; then
    echo "📦 Installing updated Python dependencies..."
    pip install -r "$REQUIREMENTS_FILE"
    echo "$new_hash" > "$REQUIREMENTS_HASH_FILE"
  else
    echo "✅ Python dependencies are up-to-date."
  fi
fi

echo "🚀 Starting Products Django Service on port $PRODUCTS_PORT... (Kafka Producer)..."
cd "$DJANGO_PRODUCTS_SERVICE"
PRODUCTS_PID=$(run_with_prefix "PRODUCTS" python manage.py runserver 0.0.0.0:$PRODUCTS_PORT)
wait_for_port $PRODUCTS_PORT "Products Django Service"

echo "👜 Starting Orders Django Service on port $ORDERS_PORT... (Consumer API)..."
cd "$DJANGO_ORDERS_SERVICE"
ORDERS_PID=$(run_with_prefix "ORDERS" python manage.py runserver 0.0.0.0:$ORDERS_PORT)
wait_for_port $ORDERS_PORT "Orders Django Service"

echo "☕ Starting Kafka Consumer (Auto-restart Enabled)..."
cd "$DJANGO_ORDERS_SERVICE"
run_kafka_consumer() {
  while true; do
    echo "▶️ Kafka Consumer Running..."
    python manage.py run_consumer || echo "⚠️ Consumer crashed, restarting..."
    sleep 5
  done
}
CONSUMER_PID=$(run_with_prefix "ORDERS_CONSUMER" run_kafka_consumer)


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
