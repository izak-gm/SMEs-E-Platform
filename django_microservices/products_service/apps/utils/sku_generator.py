import random
import string
import uuid


def generate_sku(prefix="PRD"):
  """
  Generate a unique SKU.
  Example:PRD-A9K2QZ-1F3B9C
  """

  random_part = "".join(random.choices(string.ascii_uppercase + string.digits, k=6))
  uuid_part = str(uuid.uuid4().hex[:6]).upper()

  return f"{prefix}-{random_part}-{uuid_part}"
