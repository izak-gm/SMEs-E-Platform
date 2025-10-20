from rest_framework import permissions

class RolePermission(permissions.BasePermission):
    allowed_roles = []

    def has_permission(self, request, view):
        user = request.user
        if not user:
            return False

        role = user.get("role")
        if role in self.allowed_roles:
            return True

        return False
