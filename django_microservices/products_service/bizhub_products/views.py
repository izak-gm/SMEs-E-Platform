from rest_framework import viewsets, permissions
from rest_framework.permissions import IsAuthenticated, AllowAny
from django_microservices.common.auth.authentication import JWTAuthentication
from django_microservices.common.auth.role_based_permissions import IsBuyer, IsSeller, IsAdmin

from .models import (
    Product, Store, SellerKYC, Brand, Category, ProductVariant, ProductImage
)
from .serializers import (
    ProductSerializer, StoreKYCSerializer, BrandSerializer,
    CategorySerializer, ProductVariantSerializer, ProductImageSerializer
)

# --- Store Views ---
class StoreViewSet(viewsets.ModelViewSet):
    queryset = Store.objects.all()
    serializer_class = ProductSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated, IsSeller]

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

    def get_permissions(self):
        """
        Allow:
        - Sellers to create/update/delete products
        - Buyers and Admins to view (GET, HEAD, OPTIONS)
        """
        if self.request.method in permissions.SAFE_METHODS:  # GET, HEAD, OPTIONS
            return [IsAuthenticated(), (IsBuyer() | IsAdmin())]
        return [IsAuthenticated(), IsSeller()]


class ProductVariantViewSet(viewsets.ModelViewSet):
    queryset = ProductVariant.objects.all()
    serializer_class = ProductVariantSerializer


class ProductImageViewSet(viewsets.ModelViewSet):
    queryset = ProductImage.objects.all()
    serializer_class = ProductImageSerializer
