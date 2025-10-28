from rest_framework import routers
from django_microservices.orders_service.bizhub_orders.views import OrderViewSet, OrderItemViewSet

router = routers.DefaultRouter()
router.register(r'orders',OrderViewSet)
router.register(r'orders/items',OrderItemViewSet)