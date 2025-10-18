import jwt
from rest_framework import authentication, exceptions
from django.conf import settings

class JWTAuthentication(authentication.BaseAuthentication):
    def authenticate(self, request):
        auth_header=request.headers.get('Authorization')
        if not auth_header:
            return None
        try:
            token=auth_header.split("")[1]
            decoded_token = jwt.decode(token, settings.JWT_SECRET, algorithms=["HS256"])
        except Exception as e:
            raise exceptions.AuthenticationFailed(f"Invalid token:{e}")
        return decoded_token, None