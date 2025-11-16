import os
from confluent_kafka import Producer
from django.apps import AppConfig
import py_eureka_client.eureka_client as eureka_client
from django.conf import settings

# --- OpenTelemetry Imports ---
from opentelemetry import trace
from opentelemetry.sdk.resources import Resource
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor
from opentelemetry.exporter.zipkin.json import ZipkinExporter
from opentelemetry.instrumentation.django import DjangoInstrumentor
from opentelemetry.instrumentation.requests import RequestsInstrumentor
from opentelemetry.propagate import set_global_textmap
from opentelemetry.propagators.b3 import B3MultiFormat


class ApiConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'bizhub_products'

    def ready(self):
        # Prevent running twice (common during development)
        if os.environ.get('RUN_MAIN') != 'true':
            return

        print("[Startup] Registering service and connecting to Kafka...")

        EUREKA_SERVER = "http://localhost:8761/eureka"
        APP_NAME = "django-products-service"
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

        # ---------------------------------------
        # ✔ Zipkin + OpenTelemetry Tracing Setup
        # ---------------------------------------

        # B3 header propagation (Spring Boot compatible)
        set_global_textmap(B3MultiFormat())

        # Create tracer provider with service name
        provider = TracerProvider(
            resource=Resource.create({"service.name": "django-products-service"})
        )

        # Zipkin exporter
        zipkin_exporter = ZipkinExporter(
            endpoint="http://localhost:9411/api/v2/spans"
        )

        # Add span processor
        provider.add_span_processor(BatchSpanProcessor(zipkin_exporter))

        # Register global tracer
        trace.set_tracer_provider(provider)

        # Auto-instrument Django + outgoing HTTP requests
        DjangoInstrumentor().instrument()
        RequestsInstrumentor().instrument()

        print("[Tracing] ✅ OpenTelemetry + Zipkin instrumentation enabled!")