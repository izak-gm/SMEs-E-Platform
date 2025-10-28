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
def upload_file_to_minio(request):
    if request.method == 'POST':
        files = request.FILES.getlist('file')  # handles multiple uploads

        if not files:
            return JsonResponse({"message": "No files uploaded"}, status=400)

        uploaded_urls = []

        for file in files:
            filename = file.name

            client.put_object(
                bucket_name,
                filename,
                file,
                length=file.size,
                content_type=file.content_type
            )

            file_url = f"{settings.MINIO_ENDPOINT}/{bucket_name}/{filename}"
            uploaded_urls.append(file_url)

        return JsonResponse({
            "message": "Files uploaded successfully",
            "urls": uploaded_urls
        })

    return JsonResponse({"message": "Invalid request method"}, status=400)

