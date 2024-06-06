#Don't use this code as we have kafka-data-ingestion
import time
import random
import string
import uuid
import json
from confluent_kafka import Producer


# Kafka broker address
bootstrap_servers = 'kafka:9092'

# Kafka topics to produce messages
topics = ['events-topic']


# Only for testing kafka
def sample_log_insertion():
    conf = {
        'bootstrap.servers': bootstrap_servers
    }
    producer = Producer(conf)

    def delivery_report(err, msg):
        if err is not None:
            print('Message delivery failed: {}'.format(err))
        else:
            print('Message delivered to {} [{}]'.format(msg.topic(), msg.partition()))

    try:
        for i in range(10):
            key = "key_" + str(i)
            value = "value_" + str(i)
            producer.produce(topics[0], key=key, value=value, callback=delivery_report)

        producer.flush()  # Wait for messages to be delivered
    except KeyboardInterrupt:
        sys.exit(1)


# Function to generate a random string
def generate_random_string(length):
    return ''.join(random.choices(string.ascii_letters + string.digits, k=length))

# Function to produce messages to Kafka topic
def produce_message(producer, topic, message):
    producer.produce(topic, message)
    producer.flush()

# Kafka Producer configuration
producer_conf = {
    'bootstrap.servers': bootstrap_servers
}

# Create Kafka Producer instance
producer = Producer(producer_conf)

# Produce messages to Kafka topics
numbers = [4, 5]
try:
    while True:
        id = random.choice(numbers)
        data = {
            "timestamp": int(time.time()),
            "name": str(uuid.uuid4()),
            "price": 2,
            "id": id
        }
        msg = json.dumps(data)
        for topic in topics:
            print(f"Sending {msg} to {topic}")
            produce_message(producer, topic, msg)
        time.sleep(1)
except KeyboardInterrupt:
    print("Producer interrupted. Exiting...")

# Flush any remaining messages and close the producer
producer.flush()
producer.close()

