from django.db import transaction
from rest_framework.exceptions import ValidationError

from django_microservices.products_service.apps.models import Product, ProductVariant, ProductImage
from django_microservices.products_service.apps.signals.product_events import send_product_published_event
from django_microservices.products_service.apps.utils.minio_client import upload_to_minio
from django_microservices.products_service.apps.utils.sku_generator import generate_sku


class ProductService:
  # create product
  @staticmethod
  @transaction.atomic
  def create_product(*, validated_data):
    variants_data = validated_data.pop('variants', [])
    images_data = validated_data.pop('images', [])

    # auto generate sku
    if not validated_data.get("sku"):
      validated_data["sku"] = generate_sku("PRD")

    # default status
    if not validated_data.get("status"):
      validated_data["status"] = Product.Status.DRAFT
    product = Product.objects.create(**validated_data)

    return product, variants_data, images_data

  @staticmethod
  @transaction.atomic
  def bulk_create_variants(product, variants_data):

    variants = []

    for v in variants_data:
      if not isinstance(v, dict):
        raise ValueError("Each variant must be an object")

      sku = v.get("sku") or generate_sku("VAR")
      variants.append(
        ProductVariant(
          product=product,
          sku=sku,
          attributes=v.get("attributes", {}),
          price=v["price"],
          stock_quantity=v.get("stock_quantity", 0),
          reserved_stock=v.get("reserved_stock", 0),
          active=v.get("active", True),
        )
      )

    return ProductVariant.objects.bulk_create(variants)

  @staticmethod
  @transaction.atomic
  def upload_images(product, images_data):
    if not images_data:
      return []
    images = []
    for file in images_data:
      url = upload_to_minio(file)
      image = ProductImage(
        product=product,
        url=url,  # already uploaded to MinIO
        alt_text=""
      )
      images.append(image)

    return ProductImage.objects.bulk_create(images)

  @staticmethod
  @transaction.atomic
  def publish_product(product):
    if not product.variants.exists():
      raise ValidationError("Product has no variants. Need at least one variant")

    if not product.images.exists():
      raise ValidationError("Product has no images. Need at least one image")

    total_stock = sum(
      v.stock_quantity for v in product.variants.all()
    )

    # ✅ FIXED LOGIC
    if total_stock <= 0:
      raise ValidationError("Product stock must be greater than zero")

    product.status = Product.Status.PUBLISHED
    product.save(update_fields=["status"])

    send_product_published_event(product)

    return product
