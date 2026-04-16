from types import SimpleNamespace

from rest_framework import authentication


class InternalServiceAuthentication(authentication.BaseAuthentication):
    def authenticate(self, request):
        auth_header = request.headers.get("Authorization")
        print("AUTH HEADER:", auth_header)  # 👈 ADD THIS

        if not auth_header:
            return None
        parts = auth_header.split(" ")
        if len(parts) != 2 or parts[0] != "Bearer":
            return None
        token = parts[1]
        print("TOKEN RECEIVED:", token)  # 👈 ADD THIS

        INTERNAL_TOKENS = {
            "NOTIFICATION_SERVICE_TOKEN": "notification-service",
            "DJANGO_ORDER_SERVICE_TOKEN": "order-service",
            "AUTH_SERVICE_TOKEN": "auth-service",

        }

        if token in INTERNAL_TOKENS:
            user = SimpleNamespace(
                id="internal-service",
                is_authenticated=True,
                is_service=True,
            )
            return user, token
        return None
