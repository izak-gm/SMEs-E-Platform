#!/bin/bash
echo "🛑 Stopping all services...by 5-8"
pkill -f "mvn spring-boot:run" || true
pkill -f "manage.py runserver" || true
ports=(8888 8090 8671 8012 8011 8222)
for port in "${ports[@]}";do
	echo "Killing process on port $port"
	sudo fuser -k ${port}/tcp 2>/dev/null || echo "No process found on port ${port}"
done
echo "✅ All backend services stopped."

