from django.conf import settings
from django.db.models.signals import post_save, post_delete
from django.dispatch import receiver

from django_microservices.common.kafka.producer import send_message
from ..models import Order


@receiver(post_save, sender=Order)
def publish_order_events(sender,instance, created, **kwargs):
    """Publish order events. Created at and updated at."""
    try:
        topic = settings.ORDER_CREATED if created else settings.ORDER_UPDATED
        event_type =settings.ORDER_CREATED if created else settings.ORDER_UPDATED
        payload = {
            "id": str(instance.id),
            "buyer_id": str(instance.buyer_id),
            "store_id": str(instance.store_id),
            "total_amount": float(instance.total_amount),  # convert Decimal to float
            "status": instance.status,
            "created_at": instance.created_at.isoformat(),  # also convert datetime to string
        }

        send_message(topic,event_type, payload)
        print(f"Published order event for {instance.id} to payment microservice as {event_type}")
        print(f"Payload is {payload}")
    except Exception as e:
        print(f"Kafka error failed to publish order {e}")
def publish_order_events_for(order, created=True):
    # manual wrapper
    publish_order_events(sender=Order, instance=order, created=created)

@receiver(post_delete,sender=Order)
def publish_order_deleted_event(sender,instance, **kwargs):
    """Publish order deleted events to payment microservice"""
    try:
        topic=settings.ORDER_DELETED
        event_type=settings.ORDER_DELETED
        payload={"id": str(instance.id)}
        send_message(topic,event_type, payload)
        print(f"Published order event for {instance.id} to payment microservice a deleted order")
    except Exception as e:
        print(f"Kafka error failed to publish order {e}")