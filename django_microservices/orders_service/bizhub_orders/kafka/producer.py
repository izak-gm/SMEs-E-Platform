from django.conf import settings
from django.db.models.signals import post_save, post_delete
from django.dispatch import receiver

from django_microservices.common.kafka.producer import send_message
from django_microservices.orders_service.bizhub_orders.models import Order


@receiver(post_save, sender=Order)
def publish_order_events(sender,instance, created, **kwargs):
    """Publish order events. Created at and updated at."""
    try:
        topic = settings.ORDER_CREATED if created else settings.ORDER_UPDATED
        event_type = "ORDER_CREATED"if created else "ORDER_UPDATED"
        payload = {
            "id": str(instance.id),
            "buyer_id":instance.buyer_id,
            "store_id":instance.store_id,
            "total_amount":instance.total_amount,
            "status":instance.status,
            "created_at":instance.created_at,
        }

        send_message(topic,event_type, payload)
        print(f"Published order event for {instance.id} to payment microservice as {event_type}")
    except Exception as e:
        print(f"Kafka error failed to publish order {e}")

@receiver(post_delete,sender=Order)
def publish_order_events(sender,instance, **kwargs):
    """Publish order deleted events to payment microservice"""
    try:
        topic=settings.ORDER_DELETED
        event_type="ORDER_DELETED"
        payload={"id": str(instance.id)}
        send_message(topic,event_type, payload)
        print(f"Published order event for {instance.id} to payment microservice a deleted order")
    except Exception as e:
        print(f"Kafka error failed to publish order {e}")