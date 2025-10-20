from rest_framework import viewsets, permissions
from rest_framework.permissions import IsAuthenticated

from .models import Product, Store, SellerKYC, Brand, Category, ProductVariant, ProductImage
from .serializers import ProductSerializer, StoreKYCSerializer, BrandSerializer, CategorySerializer, ProductVariantSerializer,ProductImageSerializer
from django_microservices.common.auth.authentication import JWTAuthentication
from django_microservices.common.auth.role_based_permissions import IsBuyer,IsSeller

# Create your views here.
class StoreViewSet(viewsets.ModelViewSet):
    queryset = Store.objects.all()
    serializer_class = ProductSerializer

    # TODO Add permissions for Authenticated and Authorized Store owner
    authentication_classes = JWTAuthentication
    permission_classes = (IsAuthenticated,IsSeller)

class StoreKYCViewSet(viewsets.ModelViewSet):
    queryset = SellerKYC.objects.all()
    serializer_class = StoreKYCSerializer

class BrandViewSet(viewsets.ModelViewSet):
    queryset = Brand.objects.all()
    serializer_class = BrandSerializer

class CategoryViewSet(viewsets.ModelViewSet):
    queryset = Category.objects.all()
    serializer_class = CategorySerializer

class ProductViewSet(viewsets.ModelViewSet):
    queryset = Product.objects.all()
    serializer_class = ProductSerializer

class ProductVariantViewSet(viewsets.ModelViewSet):
    queryset = ProductVariant.objects.all()
    serializer_class = ProductVariantSerializer

class ProductImageViewSet(viewsets.ModelViewSet):
    queryset = ProductImage.objects.all()
    serializer_class = ProductImageSerializer