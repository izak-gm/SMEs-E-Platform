from .permissions import RolePermission

class IsAdmin(RolePermission):
    allowed_roles = ["SUPER_ADMIN","ADMIN"]

class IsBuyer(RolePermission):
    allowed_roles = ["USER"]

class IsSeller(RolePermission):
    allowed_roles = ["SELLER"]
