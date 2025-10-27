from django.conf import settings
from django.db.models.signals import post_save, post_delete
from django.dispatch import receiver
from django_microservices.common.kafka.producer import send_message
from ..models import Product

@receiver(post_save, sender=Product)
def publish_product_events(sender, instance, created, **kwargs):
    """
       Publish product created or updated events to Kafka
    """
    try:
        topic = settings.PRODUCT_CREATED if created else settings.PRODUCT_UPDATED
        event_type = "PRODUCT_CREATED" if created else "PRODUCT_UPDATED"

        payload = {
            "id": str(instance.id),
            "name": instance.name,
            "base_price": float(instance.base_price),
            "description": instance.description,
            "is_active": instance.is_active,
            "updated_at": instance.updated_at.isoformat() if instance.updated_at else None,
        }

        send_message(topic, event_type, payload)
        print(f"[Kafka] ✅ Sent {event_type} for Product {instance.id}")

    except Exception as e:
        print(f"[Kafka ERROR] Failed to publish {e}")


@receiver(post_delete, sender=Product)
def publish_product_deleted(sender, instance, **kwargs):
    """
     Publish product deleted event to Kafka
     """
    try:
        topic = settings.PRODUCT_DELETED
        event_type = "PRODUCT_DELETED"

        payload = {"id": str(instance.id)}

        send_message(topic, event_type, payload)
        print(f"[Kafka] ✅ Sent {event_type} for Product {instance.id}")

    except Exception as e:
        print(f"[Kafka ERROR] Failed to publish PRODUCT_DELETED: {e}")