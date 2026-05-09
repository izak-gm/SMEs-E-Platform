from django.urls import include, path
from rest_framework.routers import DefaultRouter

from .minio.views_minio import upload_file_to_minio
from .views import ProductViewSet, StoreViewSet, StoreKYCViewSet, BrandViewSet, CategoryViewSet, ProductVariantViewSet, \
    ProductImageViewSet

router = DefaultRouter()
router.register(r'stores', StoreViewSet, basename='store')
router.register(r'store-kyc', StoreKYCViewSet, basename='store_kyc')
router.register(r'brands', BrandViewSet, basename='brand')
router.register(r'categories', CategoryViewSet, basename="category")
router.register(r'products', ProductViewSet, basename='product')
router.register(r'product-variants', ProductVariantViewSet, basename='variant')
router.register(r'product-images', ProductImageViewSet, basename='image')

urlpatterns = [
    path('', include(router.urls)),
    path('upload/', upload_file_to_minio, name="upload_file_to_minio")
]
