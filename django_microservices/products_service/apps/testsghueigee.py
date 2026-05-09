# from django.db.models import UUIDField
# from django.testsdss import TestCase
#
#
# # Create your testsdss here.
# class createStoreTest(TestCase):
#
#     def test_create_store(self):
#         owner_id = UUIDField.generated
#         print(owner_id)
#         self.client.post('v1/api/bizhub/seller/store/', data={
#             "owner_id", "name", "slug", "Fruit vendor"
#         })
#
#
# from rest_framework.testsdss import APITestCase
#
# from .models import Store
#
#
# class StoreViewSetTest(APITestCase):
#
#     def setUp(self):
#         self.seller = User.objects.create_user(username="seller", password="pass123")
#         self.admin = User.objects.create_user(
#             username="admin",
#             password="pass123",
#             is_staff=True
#         )
#         self.normal_user = User.objects.create_user(username="user", password="pass123")
#
#         self.store_data = {
#             "name": "Test Store",
#             "description": "Test Description"
#         }
#
#     # 🔹 CREATE STORE
#
#     def test_create_store_success(self):
#         self.client.force_authenticate(user=self.seller)
#
#         url = reverse("store-list")
#         response = self.client.post(url, self.store_data)
#
#         self.assertEqual(response.status_code, status.HTTP_201_CREATED)
#         self.assertEqual(Store.objects.count(), 1)
#         self.assertEqual(Store.objects.first().owner_id, self.seller.id)
#
#     def test_create_store_requires_authentication(self):
#         url = reverse("store-list")
#         response = self.client.post(url, self.store_data)
#
#         self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)
#
#     def test_create_store_requires_seller_role(self):
#         self.client.force_authenticate(user=self.normal_user)
#
#         url = reverse("store-list")
#         response = self.client.post(url, self.store_data)
#
#         self.assertEqual(response.status_code, status.HTTP_403_FORBIDDEN)
#
#     # 🔹 APPROVE STORE
#
#     def test_admin_can_approve_store(self):
#         store = Store.objects.create(
#             name="Store1",
#             owner_id=self.seller.id,
#             status=Store.Status.PENDING
#         )
#
#         self.client.force_authenticate(user=self.admin)
#
#         url = reverse("store-approve", args=[store.id])
#         response = self.client.post(url)
#
#         store.refresh_from_db()
#
#         self.assertEqual(response.status_code, status.HTTP_200_OK)
#         self.assertEqual(store.status, Store.Status.ACTIVE)
#         self.assertEqual(response.data["message"], "Store Approved")
#
#     def test_non_admin_cannot_approve_store(self):
#         store = Store.objects.create(
#             name="Store1",
#             owner_id=self.seller.id,
#             status=Store.Status.PENDING
#         )
#
#         self.client.force_authenticate(user=self.seller)
#
#         url = reverse("store-approve", args=[store.id])
#         response = self.client.post(url)
#
#         self.assertEqual(response.status_code, status.HTTP_403_FORBIDDEN)
#
#     def test_cannot_approve_already_active_store(self):
#         store = Store.objects.create(
#             name="Store1",
#             owner_id=self.seller.id,
#             status=Store.Status.ACTIVE
#         )
#
#         self.client.force_authenticate(user=self.admin)
#
#         url = reverse("store-approve", args=[store.id])
#         response = self.client.post(url)
#
#         self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
#         self.assertEqual(response.data["message"], "Store Already Approved")
#
#     def test_approve_nonexistent_store(self):
#         self.client.force_authenticate(user=self.admin)
#
#         url = reverse("store-approve", args=[999])
#         response = self.client.post(url)
#
#         self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
#
#
# from django.urls import reverse
# from rest_framework import status
# from rest_framework.testsdss import APITestCase
# from django.contrib.auth.models import User
#
#
# class ProductViewSetTest(APITestCase):
#
#     def setUp(self):
#         self.seller = User.objects.create_user(username="seller", password="pass123")
#         self.user = User.objects.create_user(username="user", password="pass123")
#
#         self.product_data = {
#             "name": "Test Product",
#             "price": 100
#         }
#
#     # 🔹 PUBLIC READ
#
#     def test_anyone_can_view_products(self):
#         url = reverse("product-list")
#
#         response = self.client.get(url)
#
#         self.assertEqual(response.status_code, status.HTTP_200_OK)
#
#     # 🔹 CREATE
#
#     def test_seller_can_create_product(self):
#         self.client.force_authenticate(user=self.seller)
#
#         url = reverse("product-list")
#         response = self.client.post(url, self.product_data)
#
#         self.assertEqual(response.status_code, status.HTTP_201_CREATED)
#
#     def test_non_seller_cannot_create_product(self):
#         self.client.force_authenticate(user=self.user)
#
#         url = reverse("product-list")
#         response = self.client.post(url, self.product_data)
#
#         self.assertEqual(response.status_code, status.HTTP_403_FORBIDDEN)
#
#     def test_unauthenticated_user_cannot_create_product(self):
#         url = reverse("product-list")
#
#         response = self.client.post(url, self.product_data)
#
#         self.assertEqual(response.status_code, status.HTTP_403_FORBIDDEN)
