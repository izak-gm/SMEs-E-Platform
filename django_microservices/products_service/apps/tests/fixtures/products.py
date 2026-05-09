import uuid

import pytest

from django_microservices.products_service.apps.models import Product, Brand, Category


@pytest.fixture
def product():
  return Product.objects.create(
    id=uuid.uuid4(),
    store=uuid.uuid4(),
    name='Test Product',
    slug='testsdss-product',
    sku='testsdss-product',
    description='Test Product',
    brand=uuid.uuid4(),
    category=uuid.uuid4(),
    base_price=200.00
  )


@pytest.fixture
def store_product():
  def create_store_product(**kwargs):
    data = {
      "id": uuid.uuid4(),
      "store": uuid.uuid4(),
      "name": 'Test Product',
      "slug": 'testsdss-product',
      "sku": 'testsdss-product',
      'description': 'Test Product',
      "brand": uuid.uuid4(),
      'category': uuid.uuid4(),
      "base_price": 200.00,
    }
    data.update(kwargs)
    return Product.objects.create(**data)

  return create_store_product


@pytest.fixture
def brand():
  return Brand.objects.create(
    id=uuid.uuid4(),
    logo_url="https://example.com/logo.png",
  )


@pytest.fixture
def category():
  return Category.objects.create(
    id=uuid.uuid4(),
    name='Test Category',
    parent=uuid.uuid4(),
  )
