import uuid

from django.db import models


class Store(models.Model):
  class Status(models.TextChoices):
    PENDING = 'pending', 'Pending'
    ACTIVE = 'active', 'Active'
    SUSPENDED = 'suspended', 'Suspended'

  id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
  owner_id = models.UUIDField()  # references auth user id
  name = models.CharField(max_length=120)
  slug = models.SlugField(unique=True)
  description = models.TextField(blank=True)
  status = models.CharField(max_length=20,
                            choices=Status.choices,
                            default=Status.PENDING)
  rating = models.DecimalField(max_digits=3, decimal_places=2, default=0)
  total_sales = models.DecimalField(max_digits=12, decimal_places=2, default=0)
  created_at = models.DateTimeField(auto_now_add=True)


class SellerKYC(models.Model):
  id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
  store = models.OneToOneField(Store, on_delete=models.CASCADE)
  doc_type = models.CharField(max_length=50)
  doc_url = models.URLField()
  status = models.CharField(max_length=20,
                            choices=[('pending', 'Pending'), ('verified', 'Verified'), ('rejected', 'Rejected')],
                            default='pending')
  submitted_at = models.DateTimeField(auto_now_add=True)


class Brand(models.Model):
  id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False,
                        )
  name = models.CharField(max_length=120, unique=True)
  logo_url = models.URLField(blank=True)


class Category(models.Model):
  id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False,
                        )
  name = models.CharField(max_length=120)
  parent = models.ForeignKey('self', null=True, blank=True, on_delete=models.SET_NULL)


# Add an updated at in the product
class Product(models.Model):
  class Status(models.TextChoices):
    DRAFT = "draft", "Draft"
    PROCESSING = "processing", "Processing"
    PUBLISHED = "published", "Published"
    READY = "ready", "Ready"
    FAILED = "failed", "Failed"

  id = models.UUIDField(primary_key=True, default=uuid.uuid4)
  store = models.ForeignKey(Store, null=True, blank=True, on_delete=models.SET_NULL, related_name="products")
  sku = models.CharField(max_length=80, unique=True)
  name = models.CharField(max_length=250)
  slug = models.SlugField(unique=True, blank=True)
  description = models.TextField(blank=True)
  brand = models.ForeignKey(Brand, null=True, blank=True, on_delete=models.SET_NULL, related_name="products")
  category = models.ForeignKey(Category, null=True, on_delete=models.SET_NULL, blank=True, related_name="products")
  base_price = models.DecimalField(max_digits=12, decimal_places=2)
  discount_price = models.DecimalField(max_digits=12, decimal_places=2, null=True,
                                       blank=True)
  is_active = models.BooleanField(default=True)
  # TODO: Auto increment views
  views = models.PositiveIntegerField(default=0)
  status = models.CharField(max_length=20, choices=Status.choices, default=Status.DRAFT, )
  created_at = models.DateTimeField(auto_now_add=True)
  updated_at = models.DateTimeField(auto_now=True)
  is_deleted = models.BooleanField(default=False)
  deleted_at = models.DateTimeField(null=True, blank=True)
  
  class Meta:
    indexes = [
      models.Index(fields=["status"]),
      models.Index(fields=["store"]),
      models.Index(fields=["category"]),
    ]

  def __str__(self):
    return self.name


class ProductVariant(models.Model):
  id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False, )
  product = models.ForeignKey(Product, related_name='variants', on_delete=models.CASCADE)
  sku = models.CharField(max_length=80)
  attributes = models.JSONField(default=dict)  # ex {"size":"M","color":"red"}
  price = models.DecimalField(max_digits=12, decimal_places=2)
  # Total stock
  stock_quantity = models.PositiveIntegerField(default=0)
  # Reserved by pending orders/carts
  reserved_stock = models.PositiveIntegerField(default=0)
  active = models.BooleanField(default=True)
  created_at = models.DateTimeField(auto_now_add=True)
  is_deleted = models.BooleanField(default=False)
  deleted_at = models.DateTimeField(null=True, blank=True)

  class Meta:
    unique_together = (
      "product",
      "sku",
    )

  @property
  def available_stock(self):
    return self.stock_quantity - self.reserved_stock

  def __str__(self):
    return self.sku


class ProductImage(models.Model):
  id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
  product = models.ForeignKey(Product, related_name='images',
                              on_delete=models.CASCADE)
  url = models.URLField()
  alt_text = models.CharField(max_length=150, blank=True)
  order = models.PositiveIntegerField(default=0)
  created_at = models.DateTimeField(auto_now_add=True)

  def __str__(self):
    return self.url
