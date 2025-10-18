import json
from django.conf import settings
from kafka import KafkaProducer, KafkaConsumer

BOOTSTRAP_KAFKA = bootstrap_servers=settings.KAFKA_BOOTSTRAP_SERVER,

def make_producer():
     return KafkaProducer(bootstrap_servers=BOOTSTRAP_KAFKA,
                        value_serializer=lambda v: json.dumps(v).encode('utf-8'))

def make_consumer(topics,group_id):
    return KafkaConsumer(*topics,bootstrap_servers=BOOTSTRAP_KAFKA,group_id=group_id,
                         value_deserializer=lambda v: json.loads(v.decode('utf-8')),)
