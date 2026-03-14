from django.urls import include, path
from rest_framework import routers
from .views import OrderViewSet, OrderItemViewSet

router = routers.DefaultRouter()
router.register(r'orders', OrderViewSet, basename='order')
router.register(r'order-items', OrderItemViewSet, basename='order_item')

urlpatterns = [
    path('',include(router.urls)),
]