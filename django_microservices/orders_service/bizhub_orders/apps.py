from confluent_kafka import Producer
from django.apps import AppConfig
import py_eureka_client.eureka_client as eureka_client
from django.conf import settings


class BizhubOrdersConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'bizhub_orders'

    def ready(self):
        EUREKA_SERVER = "http://localhost:8761/eureka"
        APP_NAME = "DJANGO_orders-microservice"
        INSTANCE_PORT = 8011

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
