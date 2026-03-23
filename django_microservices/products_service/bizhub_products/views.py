from django.shortcuts import get_object_or_404
from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from django_microservices.common.auth.authentication import JWTAuthentication
from django_microservices.common.auth.role_based_permissions import IsSeller, IsAdmin
from .models import (
    Product, Store, SellerKYC, Brand, Category, ProductVariant, ProductImage
)
from .serializers import (
    ProductSerializer, StoreKYCSerializer, BrandSerializer, StoreSerializer,
    CategorySerializer, ProductVariantSerializer, ProductImageSerializer
)


# --- Store Views ---
class StoreViewSet(viewsets.ModelViewSet):
    queryset = Store.objects.all()
    serializer_class = StoreSerializer
    authentication_classes = [JWTAuthentication]

    def get_permissions(self):
        if self.action == "create":
            return [IsAuthenticated(), IsSeller()]
        if self.action == "approve":
            return [IsAuthenticated(), IsAdmin()]
        else:
            return [IsAuthenticated()]

    def perform_create(self, serializer):
        # Assuming your JWT token has user.id
        serializer.save(owner_id=self.request.user.id)

    @action(detail=True, methods=["post"], url_path="approve")
    def approve(self, request, pk=None):
        store = get_object_or_404(Store, pk=pk)
        if store.status == Store.Status.ACTIVE:
            return Response(
                {"message": "Store Already Approved"},
                status=status.HTTP_400_BAD_REQUEST
            )
        store.status = Store.Status.ACTIVE
        store.save()

        # TODO: Send an email after that
        return Response({"message": "Store Approved"})


class StoreKYCViewSet(viewsets.ModelViewSet):
    queryset = SellerKYC.objects.all()
    serializer_class = StoreKYCSerializer


# --- Common Models ---
class BrandViewSet(viewsets.ModelViewSet):
    queryset = Brand.objects.all()
    serializer_class = BrandSerializer


class CategoryViewSet(viewsets.ModelViewSet):
    queryset = Category.objects.all()
    serializer_class = CategorySerializer


# --- Product Views ---
class ProductViewSet(viewsets.ModelViewSet):
    queryset = Product.objects.all()
    serializer_class = ProductSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsSeller]  # only sellers can post, put, delete

    def get_permissions(self):
        if self.request.method in ['GET', 'HEAD']:
            return []  # Anyone can view
        return [IsSeller()]


class ProductVariantViewSet(viewsets.ModelViewSet):
    queryset = ProductVariant.objects.all()
    serializer_class = ProductVariantSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsSeller]


class ProductImageViewSet(viewsets.ModelViewSet):
    queryset = ProductImage.objects.all()
    serializer_class = ProductImageSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsSeller]
