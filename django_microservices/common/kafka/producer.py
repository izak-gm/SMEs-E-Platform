import json
from django.conf import settings
from confluent_kafka import Producer, Consumer

# NOTE: remove trailing comma — it makes this a tuple, not a string
BOOTSTRAP_KAFKA = settings.KAFKA_BOOTSTRAP_SERVER

def make_producer():
    """
    Creates a Kafka producer instance.
    Serializes values to JSON before sending.
    """
    return Producer({
        "bootstrap.servers":BOOTSTRAP_KAFKA,
        "acks":'all',                # ensure message delivery
        "retries":3,                 # retry on failure
        "linger.ms":10,
    })

def delivery_report(err, msg):
    """Callback for Kafka message delivery."""
    if err is not None:
        print(f"[Kafka ERROR] Delivery failed: {err}")
    else:
        print(f"[Kafka] ✅ Message delivered to {msg.topic()} [{msg.partition()}]")

def send_message(topic:str,event_type:str, data: dict):
    """
    Serialize to JSON and send message.
    """
    try:
        producer = make_producer()
        event = {
            "event_type": event_type,
            "data": data
        }
        producer.produce(
            topic,
            json.dumps(event).encode("utf-8"),
            callback=delivery_report)
        producer.flush(3) # wait for 3 seconds for delivery
        print(f"[Kafka] Sent message to topic '{topic}'")
    except Exception as e:
        print(f"[Kafka ERROR] Failed to send to {topic}: {e}")

