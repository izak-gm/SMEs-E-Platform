#!/bin/bash

# ----------------------------
# Load environment variables from .env
# ----------------------------
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs )
else
  echo ".env file not found! Exiting"
fi

echo "Stopping all services...by 5-8"

pkill -f "mvn spring-boot:run" || true
pkill -f "manage.py runserver" || true

# Build a dynamic list of ports from environment variables
ports=(
  "$CONFIG_PORT"
  "$AUTH_PORT"
  "$DISCOVERY_PORT"
  "$PRODUCTS_PORT"
  "$ORDERS_PORT"
  "$GATEWAY_PORT"
  "$PAYMENT_PORT"
  "$NOTIFICATION_PORT"
)
for port in "${ports[@]}";do
  if [ -n "$port}" ]; then
	  echo "Killing process on port $port"
	  sudo fuser -k ${port}/tcp 2>/dev/null || echo "No process found on port ${port}"
  fi
done
echo "All backend services stopped."

