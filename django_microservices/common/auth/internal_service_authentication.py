from types import SimpleNamespace

from rest_framework import authentication


class InternalServiceAuthentication(authentication.BaseAuthentication):
  def authenticate(self, request):
    api_key = request.headers.get("X-API-KEY")
    print("API KEY RECEIVED:", api_key)

    if not api_key:
      return None

    from django.conf import settings

    INTERNAL_SERVICE_TOKENS = {
      "notification-service": settings.NOTIFICATION_SERVICE_API_KEY,
      "django-order-service": settings.DJANGO_ORDER_SERVICE_API_KEY,
      "auth-service": settings.AUTH_SERVICE_API_KEY,
    }

    service_name = None

    for name, token in INTERNAL_SERVICE_TOKENS.items():
      if token == api_key:
        service_name = name
        break

    if service_name:
      user = SimpleNamespace(
        id="internal-service",
        is_authenticated=True,
        is_service=True,
        service_name=service_name,
      )
      return user, api_key
    return None
