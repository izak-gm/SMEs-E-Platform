from rest_framework.routers import DefaultRouter
from .views import ProductViewSet, StoreViewSet, StoreKYCViewSet, BrandViewSet, CategoryViewSet, ProductVariantViewSet, ProductImageViewSet

router = DefaultRouter()
router.register(r'store',StoreViewSet)
router.register(r'storeKYC',StoreKYCViewSet)
router.register(r'brand',BrandViewSet)
router.register(r'category',CategoryViewSet)
router.register(r'products', ProductViewSet)
router.register(r'productVariants', ProductVariantViewSet)
router.register(r'productImages', ProductImageViewSet)
urlpatterns = router.urls