from django.shortcuts import render
from rest_framework import generics
from .models import Products,Orders
from .serializers import ProductSerializer, OrderSerializer


# Create your views here.
class ProductList(generics.ListCreateAPIView):
    queryset = Products.objects.all()
    serializer_class = ProductSerializer

class OrderList(generics.ListCreateAPIView):
    queryset = Orders.objects.all()
    serializer_class = OrderSerializer
