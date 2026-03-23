from rest_framework import serializers

from .models import Order, OrderItem, ProductCache


class OrderItemSerializer(serializers.ModelSerializer):
    class Meta:
        model = OrderItem
        fields = ['id', 'product_id', 'quantity', 'price']


class OrderSerializer(serializers.ModelSerializer):
    items = OrderItemSerializer(many=True, write_only=True)
    order_items = OrderItemSerializer(source='items', many=True, read_only=True)

    class Meta:
        model = Order
        fields = ['id', 'buyer_id', 'store_id', 'total_amount', 'status', 'created_at', 'items', 'order_items']
        read_only_fields = ['total_amount', 'status', 'created_at']

    def create(self, validated_data):
        items_data = validated_data.pop('items')

        total = 0
        order_items = []

        for item in items_data:
            product_id = item['product_id']

            try:
                product = ProductCache.objects.get(product_id=product_id, is_active=True)
            except ProductCache.DoesNotExist:
                raise serializers.ValidationError(f'Product {product_id} does not exist in the ProductCache')

            price = product.base_price
            total += item['quantity'] * price

            order_items.append({
                "product_id": product_id,
                "quantity": item['quantity'],
                "price": price
            })

        # Create order ONCE with correct total
        order = Order.objects.create(total_amount=total, **validated_data)

        for item in order_items:
            OrderItem.objects.create(order=order, **item)

        print(f"The order made is : ", order)
        return order
