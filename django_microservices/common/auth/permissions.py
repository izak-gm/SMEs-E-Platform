from rest_framework import permissions

class RolePermission(permissions.BasePermission):
    allowed_roles = []

    def has_permission(self, request, view):
        user = getattr(request, "user", None)
        if not user:
            return False

        # Handle dict-based JWT user
        if isinstance(user, dict):
            roles = user.get("roles", [])
        else:
            # Handle Django User object
            if not getattr(user, "is_authenticated", False):
                return False
            roles = getattr(user, "roles", [])
            if isinstance(roles, str):
                roles = [roles]

        # Normalize role names
        normalized_roles = [r.replace("ROLE_", "") for r in roles]

        # Debugging (optional)
        print("Roles from token:", normalized_roles)
        print("Allowed roles for view:", self.allowed_roles)

        return any(role in self.allowed_roles for role in normalized_roles)
