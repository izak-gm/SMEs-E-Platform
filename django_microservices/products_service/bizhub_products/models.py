import uuid

from django.contrib.auth import get_user_model
from django.db import models
Owner =get_user_model()

class Store(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    owner_id =models.ForeignKey(Owner,on_delete=models.CASCADE)  # references auth user id
    name = models.CharField(max_length=120)
    slug = models.SlugField(unique=True)
    description = models.TextField(blank=True)
    status = models.CharField(max_length=20,
    choices=[('pending','Pending'),('active','Active'),('suspended','Suspended')],
    default='pending')
    rating = models.DecimalField(max_digits=3, decimal_places=2, default=0)
    total_sales = models.DecimalField(max_digits=12, decimal_places=2, default=0)
    created_at = models.DateTimeField(auto_now_add=True)

class SellerKYC(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    store = models.OneToOneField(Store, on_delete=models.CASCADE)
    doc_type = models.CharField(max_length=50)
    doc_url = models.URLField()
    status = models.CharField(max_length=20,
    choices=[('pending','Pending'),('verified','Verified'),('rejected','Rejected')],
    default='pending')
    submitted_at = models.DateTimeField(auto_now_add=True)

class Brand(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4)
    name = models.CharField(max_length=120, unique=True)
    logo_url = models.URLField(blank=True)

class Category(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4)
    name = models.CharField(max_length=120)
    parent = models.ForeignKey('self', null=True, blank=True, on_delete=models.SET_NULL)

class Product(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4)
    store = models.ForeignKey(Store, null=True, blank=True, on_delete=models.SET_NULL)
    sku = models.CharField(max_length=80, unique=True)
    name = models.CharField(max_length=250)
    slug = models.SlugField(unique=True)
    description = models.TextField(blank=True)
    brand = models.ForeignKey(Brand, null=True, on_delete=models.SET_NULL)
    category = models.ForeignKey(Category, null=True, on_delete=models.SET_NULL)
    base_price = models.DecimalField(max_digits=12, decimal_places=2)
    discount_price = models.DecimalField(max_digits=12, decimal_places=2, null=True,
    blank=True)
    is_active = models.BooleanField(default=True)
    views = models.PositiveIntegerField(default=0)
    created_at = models.DateTimeField(auto_now_add=True)

class ProductVariant(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4)
    product = models.ForeignKey(Product, related_name='variants',
    on_delete=models.CASCADE)
    sku = models.CharField(max_length=80)
    attributes = models.JSONField(default=dict) # ex {"size":"M","color":"red"}
    price = models.DecimalField(max_digits=12, decimal_places=2)
    stock = models.IntegerField(default=0)
    active = models.BooleanField(default=True)

class ProductImage(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4)
    product = models.ForeignKey(Product, related_name='images',
    on_delete=models.CASCADE)
    url = models.URLField()
    alt_text = models.CharField(max_length=150, blank=True)
    order = models.IntegerField(default=0)