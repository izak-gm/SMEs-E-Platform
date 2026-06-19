import uuid

from django.conf import settings
from minio import Minio

client = Minio(
  endpoint=settings.MINIO_ENDPOINT,
  access_key=settings.MINIO_ACCESS_KEY,
  secret_key=settings.MINIO_SECRET_KEY,
  secure=False
)

bucket_name = settings.MINIO_BUCKET

if not client.bucket_exists(bucket_name):
  client.make_bucket(bucket_name)


def upload_to_minio(file):
  file.file.seek(0)

  ext = file.name.split(".")[-1]
  filename = f"products/{uuid.uuid4()}.{ext}"

  client.put_object(
    bucket_name,
    filename,
    file.file,
    length=file.size,
    content_type=file.content_type,
  )

  return f"{settings.MINIO_ENDPOINT}/{bucket_name}/{filename}"
