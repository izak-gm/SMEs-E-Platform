import uuid

import pytest

from django_microservices.products_service.apps.models import Store


# -----------------------------------
# BASIC STORE FIXTURE
# -----------------------------------


# -----------------------------------
# STORE WITH SELLER OWNER (MATCH MOCK JWT)
# -----------------------------------
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


# -----------------------------------
# ACTIVE STORE
# -----------------------------------
@pytest.fixture
def active_store():
  return Store.objects.create(
    id=uuid.uuid4(),
    name="Active Store",
    description="Already approved",
    owner_id=uuid.uuid4(),
    status=Store.Status.ACTIVE
  )


# -----------------------------------
# MULTIPLE STORES
# -----------------------------------
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


# -----------------------------------
# STORE FACTORY (FLEXIBLE)
# -----------------------------------
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
