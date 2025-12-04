from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated

from django_microservices.common.auth.authentication import JWTAuthentication
from django_microservices.common.auth.role_based_permissions import IsSeller
from .signals.product_events import publish_product_events, publish_product_deleted
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
    permission_classes = [IsAuthenticated, IsSeller]

    def perform_create(self, serializer):
        # Assuming your JWT token has user.id
        serializer.save(owner_id=self.request.user.id)

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
