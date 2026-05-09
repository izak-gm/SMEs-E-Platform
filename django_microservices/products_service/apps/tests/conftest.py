import uuid
from unittest.mock import patch

import pytest

from django_microservices.products_service.apps.models import Store


class MockUser:
  def __init__(self, role=""):
    self.id = uuid.uuid4()
    self.role = role
    self.roles = [role]
    self.is_authenticated = True
    self.email = 'izak@gmail.com'


@pytest.fixture
def mock_seller_auth():
  with patch('django_microservices.common.auth.authentication.JWTAuthentication.authenticate') as mock:
    mock.return_value = (MockUser(role='SELLER'), None)
    yield mock


@pytest.fixture
def mock_admin_auth():
  with patch('django_microservices.common.auth.authentication.JWTAuthentication.authenticate') as mock:
    mock.return_value = (MockUser(role="ADMIN"), None)
    yield mock


@pytest.fixture
def mock_buyer_auth():
  with patch('django_microservices.common.auth.authentication.JWTAuthentication.authenticate') as mock:
    mock.return_value = (MockUser(role="BUYER"), None)
    yield mock


# Stores
@pytest.fixture
def store():
  return Store.objects.create(
    id=uuid.uuid4(),
    name="Test Store",
    description="Default store",
    owner_id=uuid.uuid4(),
    status=Store.Status.PENDING
  )


@pytest.fixture
def store_factory():
  def create_store(**kwargs):
    data = {
      "id": uuid.uuid4(),
      "name": "Factory Store",
      "description": "Generated store",
      "owner_id": uuid.uuid4(),
      "status": Store.Status.PENDING,
    }

    data.update(kwargs)
    return Store.objects.create(**data)

  return create_store


@pytest.fixture
def seller_store():
  seller_id = uuid.UUID("11111111-1111-1111-1111-111111111111")

  return Store.objects.create(
    id=uuid.uuid4(),
    name="Seller Store",
    description="Owned by seller",
    owner_id=seller_id,
    status=Store.Status.PENDING
  )


@pytest.fixture
def active_store():
  return Store.objects.create(
    id=uuid.uuid4(),
    name="Active Store",
    description="Already approved",
    owner_id=uuid.uuid4(),
    status=Store.Status.ACTIVE
  )


@pytest.fixture
def multiple_stores():
  stores = []

  for i in range(3):
    stores.append(
      Store.objects.create(
        id=uuid.uuid4(),
        name=f"Store {i}",
        description=f"Store number {i}",
        owner_id=uuid.uuid4(),
        status=Store.Status.PENDING
      )
    )

  return stores
