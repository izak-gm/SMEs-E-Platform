from django.shortcuts import render
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated

from django_microservices.common.auth.authentication import JWTAuthentication
from django_microservices.orders_service.bizhub_orders.models import Order, OrderItem
from django_microservices.orders_service.bizhub_orders.serializers import OrderSerializer, OrderDetailSerializer


# Create your views here.
class OrderViewSet(viewsets.ModelViewSet):
    queryset = Order.objects.all()
    serializer_class = OrderSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated]


class OrderItemViewSet(viewsets.ModelViewSet):
    queryset = OrderItem.objects.all()
    serializer_class = OrderDetailSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated]