import os
from pathlib import Path
from dotenv import load_dotenv

# Base directory for this Django service
BASE_DIR = Path(__file__).resolve().parent.parent

# Path to the shared .env file in django_microservices/
ENV_PATH = BASE_DIR.parent / ".env"

# Load environment variables
load_dotenv(ENV_PATH)

# Access variables
JWT_SECRET = os.getenv("JWT_SECRET")
JWT_ALGORITHM = os.getenv("JWT_ALGORITHM", "HS256")
