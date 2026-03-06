from django.db import transaction
from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from django_microservices.common.auth.authentication import JWTAuthentication
from .models import Order, OrderItem
from .serializers import OrderSerializer, OrderItemSerializer
from .signals.order_events import publish_order_events, publish_order_deleted_event, publish_order_events_for


# Create your views here.
class OrderViewSet(viewsets.ModelViewSet):
    queryset = Order.objects.all()
    serializer_class = OrderSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated]

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)

        order = serializer.save()  # 👈 calls OrderSerializer.create()
        publish_order_events_for(order)
        return Response(
            {
                "message": "Successfully ordered",
                "order": OrderSerializer(order).data
            },
            status=status.HTTP_201_CREATED
        )

class OrderItemViewSet(viewsets.ModelViewSet):
    queryset = OrderItem.objects.all()
    serializer_class = OrderItemSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated]