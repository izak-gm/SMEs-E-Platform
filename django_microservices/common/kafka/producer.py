import json
from django.conf import settings
from kafka import KafkaProducer, KafkaConsumer

# NOTE: remove trailing comma — it makes this a tuple, not a string
BOOTSTRAP_KAFKA = settings.KAFKA_BOOTSTRAP_SERVER

def make_producer():
    """
    Creates a Kafka producer instance.
    Serializes values to JSON before sending.
    """
    return KafkaProducer(
        bootstrap_servers=BOOTSTRAP_KAFKA,
        value_serializer=lambda v: json.dumps(v).encode("utf-8"),
        acks='all',                # ensure message delivery
        retries=3,                 # retry on failure
        linger_ms=10,              # slight batching delay to improve throughput
    )


def make_consumer(topics, group_id):
    """
    Creates a Kafka consumer subscribed to the given topics.
    Deserializes JSON messages automatically.
    """
    consumer = KafkaConsumer(
        *topics,
        bootstrap_servers=BOOTSTRAP_KAFKA,
        group_id=group_id,
        value_deserializer=lambda v: json.loads(v.decode("utf-8")),
        auto_offset_reset="earliest",  # start from beginning if no offset stored
        enable_auto_commit=True,
    )
    return consumer
