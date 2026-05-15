from django.db import transaction
from rest_framework.exceptions import ValidationError

from django_microservices.products_service.apps.models import Product, ProductVariant, ProductImage
from django_microservices.products_service.apps.signals.product_events import send_product_published_event
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

    return product

  @staticmethod
  @transaction.atomic
  def bulk_create_variants(product, variants_data):
    variants = []
    for v in variants_data:
      sku = v.get("sku")
      if not sku:
        sku = generate_sku("VAR")

      variant = ProductVariant(
        product=product,
        sku=sku,
        attributes=v.get("attributes", {}),
        price=v["price"],
        stock_quantity=v.get("stock_quantity", 0),
        reserved_stock=v.get("reserved_stock", 0),
        active=v.get("active", True),
      )

      variants.append(variant)

    return ProductVariant.objects.bulk_create(variants)

  @staticmethod
  @transaction.atomic
  def upload_images(product, images_data):
    if not images_data:
      return []
    images = []
    for i in images_data:
      if not i.get("url"):
        raise ValidationError("Image URL required")

      image = ProductImage(
        product=product,
        url=i["url"],  # already uploaded to MinIO
        alt_text=i.get("alt_text", ""),
      )
      images.append(image)

    return ProductImage.objects.bulk_create(images)

  @staticmethod
  @transaction.atomic
  def publish_product(product):
    # check variants
    if not product.variants.exists():
      raise ValidationError("Product has no variants. Need at least one variant")
    # images
    if not product.images.exists():
      raise ValidationError("Product has no images. Need at least one image")

    # stock
    total_stock = sum(
      variant.stock_quantity
      for variant in product.variants.all())
    if not total_stock <= 0:
      raise ValidationError("Product stock cannot be less than zero")

    product.status = Product.Status.PUBLISHED
    product.save(
      update_fields=["status"]
    )
    send_product_published_event(product)
    return product
