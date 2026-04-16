from .permissions import IsServiceOrRolePermission


class IsAdmin(IsServiceOrRolePermission):
    allowed_roles = ["SUPER_ADMIN", "ADMIN"]


class IsBuyer(IsServiceOrRolePermission):
    allowed_roles = ["USER"]


class IsSeller(IsServiceOrRolePermission):
    allowed_roles = ["SELLER"]
