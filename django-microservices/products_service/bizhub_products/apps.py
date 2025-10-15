from django.apps import AppConfig
import py_eureka_client.eureka_client as eureka_client


class ApiConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'bizhub_products'

    def ready(self):
        EUREKA_SERVER = "http://localhost:8761/eureka"
        APP_NAME = "products-microservice"
        INSTANCE_PORT = 8012

        eureka_client.init(
            eureka_server=EUREKA_SERVER,
            app_name=APP_NAME,
            instance_port=INSTANCE_PORT
        )
