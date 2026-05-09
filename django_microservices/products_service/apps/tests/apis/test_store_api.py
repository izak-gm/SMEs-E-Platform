import pytest
from django.urls import reverse
from rest_framework import status


@pytest.mark.django_db
class TestStoreAPI:
  def test_unauthenticated_cannot_create_store(self, client, store_factory):
    url = reverse('store-list')
    response = client.post(url)
    assert response.status_code == status.HTTP_403_FORBIDDEN

  def test_create_store(self, client, mock_seller_auth):
    url = reverse('store-list')
    payload = {
      "name": "Test Store",
      "slug": "tests-store",
      "description": "Test description",
    }
    response = client.post(url, data=payload)
    print(response.wsgi_request.user)
    print(response.status_code)
    print(response.data)

    assert response.status_code == 201

  def test_admin_can_approve_store(self, client, mock_admin_auth, seller_store):
    """Test that admin can approve pending stores"""
    url = f"/v1/api/bizhub/stores/{seller_store.id}/approve/"
    print(url)
    response = client.post(url, content_type='application/json')
    print(response.wsgi_request.user)
    print(response.status_code)
    print(response.data)

    assert response.status_code == status.HTTP_200_OK

  def test_seller_can_update_store(self, client, mock_seller_auth, seller_store):
    """Test that seller can update pending stores"""
    url = f"/v1/api/bizhub/stores/{seller_store.id}/"
    payload = {
      "name": "Test Store",
      "slug": "tests-store",
      "description": "Test description2",
    }
    response = client.put(url, payload, content_type='application/json')
    print(response.wsgi_request.user)
    print(response.status_code)
    print(response.data)

    assert response.status_code == status.HTTP_200_OK

  # Get tests for store
  def test_all_users_can_see_stores(self, client, mock_admin_auth, mock_seller_auth, mock_buyer_auth, seller_store):
    """Test that all users can see pending and active stores"""
    url = f"/v1/api/bizhub/stores/"
    response = client.get(url, content_type='application/json')
    assert response.status_code == status.HTTP_200_OK

  def test_users_can_get_store_list(self, client, mock_admin_auth, mock_seller_auth, mock_buyer_auth, seller_store):
    """Test that admin can see pending and active stores"""
    url = f"/v1/api/bizhub/stores/{seller_store.id}/"
    response = client.get(url)
    assert response.status_code == status.HTTP_200_OK
