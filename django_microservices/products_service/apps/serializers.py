from rest_framework import serializers
from rest_framework.exceptions import ValidationError

from .models import Product, Store, SellerKYC, Brand, Category, ProductVariant, ProductImage


class StoreSerializer(serializers.ModelSerializer):
  class Meta:
    model = Store
    fields = ['id', 'owner_id', 'name', 'slug', 'description', 'status', 'rating', 'total_sales', 'created_at']
    read_only_fields = ['owner_id']


class StoreKYCSerializer(serializers.ModelSerializer):
  class Meta:
    model = SellerKYC
    fields = '__all__'


class BrandSerializer(serializers.ModelSerializer):
  class Meta:
    model = Brand
    fields = '__all__'


class CategorySerializer(serializers.ModelSerializer):
  class Meta:
    model = Category
    fields = '__all__'


class ProductImageSerializer(serializers.ModelSerializer):
  class Meta:
    model = ProductImage
    fields = '__all__'
    extra_kwargs = {'product': {'read_only': True}}


class ProductVariantSerializer(serializers.ModelSerializer):
  class Meta:
    model = ProductVariant
    fields = '__all__'
    extra_kwargs = {'product': {'read_only': True}}


class ProductSerializer(serializers.ModelSerializer):
  variants = ProductVariantSerializer(many=True, required=False, read_only=True)
  images = ProductImageSerializer(many=True, required=False, read_only=True)

  store = serializers.PrimaryKeyRelatedField(queryset=Store.objects.all(),
                                             required=True,
                                             allow_null=False)

  brand = serializers.PrimaryKeyRelatedField(queryset=Brand.objects.all(), required=True,
                                             allow_null=True)
  category = serializers.PrimaryKeyRelatedField(queryset=Category.objects.all(), required=True,
                                                allow_null=True)

  class Meta:
    model = Product
    fields = [
      'store', 'name', 'slug', 'description',
      'brand', 'category', 'base_price', 'discount_price',
      'is_active', 'views', 'created_at', "updated_at",
      'variants', 'images'
    ]
    read_only_fields = ['id', "sku", "views", "created_at", "updated_at"]

  # validating the store
  def validate_store(self, store):
    request = self.context.get('request')
    if not request or not request.user.is_authenticated:
      raise ValidationError('You need to login first')
    if store.status != store.Status.ACTIVE:
      raise ValidationError('Store must be active')
    print(store)
    print(store.owner_id)
    print(request.user)
    print(request.user.id)
    # if store.owner != request.user:
    #   raise ValidationError('You do not own this store')

    return store
