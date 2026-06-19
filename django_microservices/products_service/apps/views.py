import json

from django.db.models import Prefetch
from django.shortcuts import get_object_or_404
from django.utils import timezone
from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.parsers import MultiPartParser, FormParser, JSONParser
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from django_microservices.common.auth.authentication import JWTAuthentication
from django_microservices.common.auth.role_based_permissions import IsSeller, IsAdmin
from .models import (
  Product, Store, SellerKYC, Brand, Category
)
from .serializers import (
  StoreKYCSerializer, BrandSerializer, StoreSerializer,
  CategorySerializer, ProductSerializer, ProductVariantSerializer
)
from .services.product_service import ProductService


# --- Store Views ---
class StoreViewSet(viewsets.ModelViewSet):
  queryset = Store.objects.all()
  serializer_class = StoreSerializer
  authentication_classes = [JWTAuthentication]

  def get_permissions(self):
    # Anyone authenticated can view stores
    if self.action in ['list', 'retrieve']:
      return [IsAuthenticated()]
    # Only sellers can create/update/destroy
    if self.action in ["create", "update", "partial_update"]:
      return [IsAuthenticated(), IsSeller()]
    # Only admins can approve/delete
    if self.action in ["destroy"]:
      return [IsAuthenticated(), IsAdmin()]
    if self.action in ["approve"]:
      return [IsAuthenticated(), IsAdmin()]

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
    return Response({"message": "Store Approved"}, status=status.HTTP_200_OK)


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
  authentication_classes = [JWTAuthentication]
  permission_classes = [IsAuthenticated, IsSeller]  # only sellers can post, put, delete
  parser_classes = [MultiPartParser, FormParser, JSONParser]
  lookup_field = 'id'

  def get_queryset(self):
    queryset = (Product.objects
                .filter(is_deleted=False)
                .prefetch_related(
      Prefetch("variants"),
      Prefetch("images")
    )
                .order_by('created_at')
                )
    return queryset

  def get_serializer_class(self):
    if self.action in ['retrieve']:
      return ProductSerializer
    return ProductSerializer

  def get_permissions(self):
    if self.action in ["list", "retrieve"]:
      return [IsAuthenticated()]

    if self.action in ["destroy", "hard_delete"]:
      return [IsAuthenticated(), IsAdmin()]

    return [IsAuthenticated(), IsSeller()]

  def create(self, request, *args, **kwargs):
    serializer = self.get_serializer(data=request.data)
    serializer.is_valid(raise_exception=True)
    product = ProductService.create_product(
      validated_data=serializer.validated_data,
    )

    return Response({
      "message": "Product Created",
      "product_id": product.id
    }, status=status.HTTP_201_CREATED)

  # adding variants
  @action(detail=True, methods=["post"], parser_classes=[JSONParser])
  def add_variants(self, request, id=None):
    product = self.get_object()
    variants = request.data.get("variants")

    # handle FormData string case
    if isinstance(variants, str):
      try:
        variants = json.loads(variants)
      except json.JSONDecodeError:
        return Response({"error": "Invalid JSON"}, status=400)

    if not isinstance(variants, list):
      return Response({"error": "variants must be a list"}, status=400)

    created = (ProductService.bulk_create_variants(
      product=product,
      variants_data=variants
    ))
    return Response({
      "message": "Variants Added",
      "count": len(created),
      "created": ProductVariantSerializer(
        created,
        many=True,
      ).data
    })

  @action(detail=True, methods=["post"], parser_classes=[MultiPartParser, FormParser], )
  def upload_images(self, request, id=None):
    product = self.get_object()
    files = request.FILES.getlist('files')
    uploaded = ProductService.upload_images(
      product=product,
      images_data=files
    )

    return Response(
      {
        "message": "Images uploaded",
        "count": len(uploaded),
      }
    )

  @action(detail=True, methods=["get"])
  def status(self, request, id=None):
    product = self.get_object()
    return Response({
      "product_id": product.id,
      "status": product.status,
    })

  def destroy(self, request, *args, **kwargs):
    product = self.get_object()

    product.is_deleted = True
    product.deleted_at = timezone.now()
    product.is_active = False
    product.save()

    return Response({
      "message": "Product deleted (soft delete)",
      "product_id": product.id
    })

  @action(detail=True, methods=["post"])
  def restore(self, request, id=None):
    product = self.get_object()

    product.is_deleted = False
    product.deleted_at = None
    product.is_active = True
    product.save()

    return Response({
      "message": "Product restored",
      "product_id": product.id
    })

  @action(detail=True, methods=["delete"], permission_classes=[IsAdmin])
  def hard_delete(self, request, id=None):
    product = self.get_object()
    product.delete()

    return Response({
      "message": "Product permanently deleted"
    })
