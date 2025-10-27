import os
from confluent_kafka import Producer
from django.apps import AppConfig
import py_eureka_client.eureka_client as eureka_client
from django.conf import settings


class ApiConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'bizhub_products'

    def ready(self):
        # Prevent running twice (common during development)
        if os.environ.get('RUN_MAIN') != 'true':
            return

        print("[Startup] Registering service and connecting to Kafka...")

        EUREKA_SERVER = "http://localhost:8761/eureka"
        APP_NAME = "DJANGO_products-microservice"
        INSTANCE_PORT = 8012

        eureka_client.init(
            eureka_server=EUREKA_SERVER,
            app_name=APP_NAME,
            instance_port=INSTANCE_PORT
        )

        try:
            producer = Producer({"bootstrap.servers": settings.KAFKA_BOOTSTRAP_SERVER})
            print(f"[Kafka] ✅ Connection successful!")
        except Exception as e:
            print(f"[Kafka] ❌ Could not connect: {e}")