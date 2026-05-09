import uuid

from django.db import transaction
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
        extra_kwargs = {
            'product': {'read_only': True}
        }


class ProductVariantSerializer(serializers.ModelSerializer):
    class Meta:
        model = ProductVariant
        fields = '__all__'
        extra_kwargs = {
            'product': {'read_only': True}
        }


class ProductSerializer(serializers.ModelSerializer):
    variants = ProductVariantSerializer(many=True, required=False)
    images = ProductImageSerializer(many=True, required=False)

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
            'id', 'store', 'sku', 'name', 'slug', 'description',
            'brand', 'category', 'base_price', 'discount_price',
            'is_active', 'views', 'created_at',
            'variants', 'images'
        ]

    def validate_store(self, store):
        request = self.context.get('request')
        if store.status != Store.Status.ACTIVE:
            raise ValidationError('Store must be active')
        try:
            user_id = uuid.UUID(str(request.user.id))
        except Exception:
            raise ValidationError("Invalid user ID in request")

        if store.owner_id != user_id:
            raise ValidationError('You do not own this store')

        return store

    def validate_sku(self, value):
        if Product.objects.filter(sku=value).exists():
            raise serializers.ValidationError("SKU already exists")
        return value

    @transaction.atomic
    def create(self, validated_data):
        variants_data = validated_data.pop('variants', [])
        images_data = validated_data.pop('images', [])

        product = Product.objects.create(**validated_data)

        ProductVariant.objects.bulk_create([
            ProductVariant(product=product, **variant)
            for variant in variants_data
        ])

        ProductImage.objects.bulk_create([
            ProductImage(product=product, **image)
            for image in images_data
        ])

        return product
