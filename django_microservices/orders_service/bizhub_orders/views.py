from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated

from django_microservices.common.auth.authentication import JWTAuthentication
from .models import Order, OrderItem
from .serializers import OrderSerializer, OrderItemSerializer


# Create your views here.
class OrderViewSet(viewsets.ModelViewSet):
    queryset = Order.objects.all()
    serializer_class = OrderSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated]


class OrderItemViewSet(viewsets.ModelViewSet):
    queryset = OrderItem.objects.all()
    serializer_class = OrderItemSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated]