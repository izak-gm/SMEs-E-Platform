#!/bin/bash
echo "🛑 Stopping all services...by 5-8"
pkill -f "mvn spring-boot:run" || true
pkill -f "manage.py runserver" || true
echo "✅ All backend services stopped."

