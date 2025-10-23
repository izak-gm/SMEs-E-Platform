from django.urls import include, path
from rest_framework.routers import DefaultRouter

from .minio.views_minio import upload_file_to_minio
from .views import ProductViewSet, StoreViewSet, StoreKYCViewSet, BrandViewSet, CategoryViewSet, ProductVariantViewSet, ProductImageViewSet

router = DefaultRouter()
router.register(r'seller/store',StoreViewSet)
router.register(r'seller/storeKYC',StoreKYCViewSet)
router.register(r'brand',BrandViewSet)
router.register(r'category',CategoryViewSet)
router.register(r'user/products', ProductViewSet, basename='user-products')
router.register(r'seller/products', ProductViewSet, basename='seller-products')
router.register(r'productVariants', ProductVariantViewSet)
router.register(r'productImages', ProductImageViewSet)


urlpatterns = [
    path('',include(router.urls)),
    path('upload/',upload_file_to_minio, name="upload_file_to_minio")
]