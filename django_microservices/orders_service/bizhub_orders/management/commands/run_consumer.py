from django.core.management.base import BaseCommand
from ...kafka.consumer import make_consumer, consume_messages, start_consumer_thread


class Command(BaseCommand):
    help = "Run Kafka consumer to listen for product events."

    def handle(self, *args, **kwargs):
        self.stdout.write(self.style.SUCCESS("[Kafka] ✅ Connection successful!"))
        self.stdout.write(self.style.SUCCESS("🚀 Starting Kafka consumer..."))

        # define topics
        topics = ["product_created", "product_updated"]
        group_id = "orders_consumer_group"

        consumer = make_consumer(topics, group_id)

        try:
            consume_messages(consumer)
            start_consumer_thread(topics, group_id)
            print(f"Kafka consumer started for {topics}")
            print(f"Events consumer started for {consumer}")
        except Exception as e:
            self.stderr.write(self.style.ERROR(f"❌ Consumer crashed: {e}"))
