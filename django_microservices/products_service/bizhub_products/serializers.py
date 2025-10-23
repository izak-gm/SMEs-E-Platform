from rest_framework import serializers
from .models import Product, Store, SellerKYC, Brand, Category, ProductVariant, ProductImage


class StoreSerializer(serializers.ModelSerializer):
    class Meta:
        model = Store
        fields = '__all__'
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

from rest_framework import serializers
from .models import Product, ProductVariant, ProductImage


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

    class Meta:
        model = Product
        fields = [
            'id', 'store', 'sku', 'name', 'slug', 'description',
            'brand', 'category', 'base_price', 'discount_price',
            'is_active', 'views', 'created_at',
            'variants', 'images'
        ]

    def create(self, validated_data):
        variants_data = validated_data.pop('variants', [])
        images_data = validated_data.pop('images', [])
        product = Product.objects.create(**validated_data)

        for variant in variants_data:
            ProductVariant.objects.create(product=product, **variant)
        for image in images_data:
            ProductImage.objects.create(product=product, **image)

        return product
