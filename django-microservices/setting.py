#jwt_token to verify token from spring boot Auth service
import os

JWT_SECRET=os.getenv("JWT_SECRET_KEY")
REST_FRAMEWORK = {
    "DEFAULT_AUTHENTICATION_CLASSES": [
    "common.authentication.JWTAuthentication",
    ]
}