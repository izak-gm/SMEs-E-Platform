from django.db import models


# Create your models here.
class Products(models.Model):
    product_id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=100)
    description = models.TextField()
    price = models.FloatField()
    quantity = models.IntegerField()
    image = models.ImageField(upload_to='images/')
    offer_price = models.FloatField()
    created = models.DateField(auto_now_add=True,)
    updated = models.DateField(auto_now=True)

    def __str__(self):
        name = self.name
        description = self.description
        price = self.price
        quantity = self.quantity

        return f'{name} , {description} - {price} - {quantity}'


class Orders(models.Model):
    order_id = models.AutoField(primary_key=True)
    product = models.ForeignKey(Products, on_delete=models.CASCADE)
    quantity = models.IntegerField()
    price = models.FloatField()
    total_amount = models.FloatField(default=0.00)
    order_type = models.CharField(max_length=100)
    created = models.DateField(auto_now_add=True)
    updated = models.DateField(auto_now=True)

    def __str__(self):
        order_type = self.order_type
        product_id = self.product.id
        quantity = self.quantity
        price = self.price

        return f'{order_type} , {product_id} - {quantity} - {price}'


class OrderDetails(models.Model):
    order = models.ForeignKey(Orders, on_delete=models.CASCADE)
    product = models.ForeignKey(Products, on_delete=models.CASCADE)
