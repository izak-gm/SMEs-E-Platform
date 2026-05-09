import pytest

from django_microservices.products_service.apps.models import Store


@pytest.mark.django_db
def test_store_model():
  return Store.objects.create(name='testsdss')
