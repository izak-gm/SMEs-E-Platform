from types import SimpleNamespace

import jwt
from django.conf import settings
from rest_framework import authentication, exceptions


class JWTAuthentication(authentication.BaseAuthentication):
    def authenticate(self, request):
        print(f'Headers: {request.headers}')
        auth_header = request.headers.get("Authorization")
        if not auth_header:
            return None  # No header → skip authentication

        parts = auth_header.split(" ")
        if len(parts) != 2 or parts[0].lower() != "bearer":
            return None
        token = parts[1]

        try:
            decoded_token = jwt.decode(token, settings.JWT_SECRET, algorithms=["HS512"])
        except jwt.ExpiredSignatureError:
            raise exceptions.AuthenticationFailed("Token has expired")
        except jwt.InvalidTokenError:
            return None
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
