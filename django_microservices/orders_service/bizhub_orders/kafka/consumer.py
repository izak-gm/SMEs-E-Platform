import json
import threading

from django.conf import settings
from confluent_kafka import Consumer

BOOTSTRAP_KAFKA = settings.KAFKA_BOOTSTRAP_SERVER
running = True
def make_consumer(topics, group_id):
    """
    Creates a Kafka consumer subscribed to the given topics.
    Deserializes JSON messages automatically.
    """
    consumer = Consumer({
        "bootstrap.servers":BOOTSTRAP_KAFKA,
        "group.id":group_id,
        "auto.offset.reset":"earliest",  # start from beginning if no offset stored
        "enable.auto.commit":True,}
    )
    consumer.subscribe(topics)
    return consumer

def consume_messages(consumer):
    """
    Poll for messages and decode JSON safely.
    """
    """Poll for messages and decode JSON safely."""
    global running
    print("[Kafka] ✅ Consumer started. Listening for messages...")
    try:
        while True:
            msg = consumer.poll(1.0)
            if msg is None:
                continue
            if msg.error():
                print(f"[Kafka ERROR] {msg.error()}")
                continue

            try:
                data = json.loads(msg.value().decode("utf-8"))
                print(f"[Kafka] Received: {data}")

                event_type = data.get("event_type")
                product_data = data.get("data",{})
                save_to_product_cache(product_data,event_type)

            except json.decoder.JSONDecodeError as e:
                print("[Kafka ERROR] Failed to decode message JSON")

            except Exception as e:
                print(f"[Kafka ERROR] Unexpected error: {e}")
    except KeyboardInterrupt:
        print(f"[Kafka ERROR] JSON decode failed: {e}")
    finally:
        consumer.close()
        print("[Kafka] 🔒 Consumer connection closed.")

def save_to_product_cache(product_data,event_type):
    from ..models import ProductCache

    """Save or update received Kafka message into ProductCache table."""
    try:
        obj,created = ProductCache.objects.update_or_create(
            product_id=product_data.get("id"),  # assuming your ProductCache has a product_id
            defaults={
                "name":product_data.get("name",""),
                "base_price":product_data.get("base_price",0),
                "description":product_data.get("description",""),
                "is_active":product_data.get("is_active",True),
            }
        )
        action="create" if created else "update"
        print(f"[Kafka] Product cache {action} object {obj}")
    except Exception as e:
        print(f"[Kafka ERROR] Failed to save product cache: {e}")

def start_consumer_thread(topics,group_id):
    """
     Starts consumer in a separate thread.
     """
    t = threading.Thread(
        target=lambda: consume_messages(make_consumer(topics, group_id)),
        daemon=True
    )
    t.start()
    print(f"[Kafka] 🔄 Consumer thread started for topics: {topics}")

def stop_consumer():
    """Stop the consumer loop gracefully."""
    global running
    running = False