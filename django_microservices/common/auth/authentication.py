from types import SimpleNamespace
import jwt
from rest_framework import authentication, exceptions
from django.conf import settings

class JWTAuthentication(authentication.BaseAuthentication):
    def authenticate(self, request):
        auth_header = request.headers.get("Authorization")
        if not auth_header:
            return None  # No header → skip authentication

        parts = auth_header.split(" ")
        if len(parts) != 2 or parts[0].lower() != "bearer":
            raise exceptions.AuthenticationFailed("Invalid Authorization header format. Expected 'Bearer <token>'")

        token = parts[1]

        try:
            decoded_token = jwt.decode(token, settings.JWT_SECRET, algorithms=["HS512"])
        except jwt.ExpiredSignatureError:
            raise exceptions.AuthenticationFailed("Token has expired")
        except jwt.InvalidTokenError:
            raise exceptions.AuthenticationFailed("Invalid token")

        # Normalize roles
        roles = decoded_token.get("roles", [])
        roles = [r.replace("ROLE_", "") for r in roles]

        # You can return a dummy user or decoded claims
        user = SimpleNamespace(
            id=decoded_token.get("id"),
            email=decoded_token.get("email"),
            roles=roles,
            is_authenticated=True
        )
        return user, token
