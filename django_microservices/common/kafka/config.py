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

def make_consumer(topics, group_id):
    """
    Creates a Kafka consumer subscribed to the given topics.
    Deserializes JSON messages automatically.
    """
    consumer = Consumer({
        "bootstrap.servers":BOOTSTRAP_KAFKA,
        "group_id":group_id,
        "auto_offset_reset":"earliest",  # start from beginning if no offset stored
        "enable_auto_commit":True,}
    )
    consumer.subscribe(topics)
    return consumer

def consume_messages(consumer):
    """
    Poll for messages and decode JSON safely.
    """
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
        except Exception as e:
            print(f"[Kafka ERROR] JSON decode failed: {e}")