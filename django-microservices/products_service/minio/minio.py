from django.conf import settings
from django.views.decorators.csrf import csrf_exempt
from minio import Minio
from django.http import JsonResponse

client=Minio(
    endpoint=settings.MINIO_ENDPOINT,
    access_key=settings.MINIO_ACCESS_KEY,
    secret_key=settings.MINIO_SECRET_KEY,
    secure=False
)
bucket_name=settings.MINIO_BUCKET

# create a bucket if it does not exist make it
if not client.bucket_exists(bucket_name):
    client.make_bucket(bucket_name)
@csrf_exempt
def upload_file(request):
    if request.method == 'POST' and request.FILES['file']:
        file = request.FILES['file']
        filename = file.name

        client.put_object(bucket_name, filename, file,length=file.size,content_type=file.content_type)
        file_url=f"products/{bucket_name}/{filename}"
        return JsonResponse({"message":"File uploaded",'url':file_url})
    return JsonResponse({"message":"File not supported"},status=400)