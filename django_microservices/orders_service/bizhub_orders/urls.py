from django.urls import include, path
from rest_framework import routers
from .views import OrderViewSet, OrderItemViewSet

router = routers.DefaultRouter()
router.register(r'orders',OrderViewSet)
router.register(r'orders/items',OrderItemViewSet)

urlpatterns = [
    path('',include(router.urls)),
]