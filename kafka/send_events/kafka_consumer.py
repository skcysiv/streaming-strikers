from confluent_kafka import Consumer, KafkaError

bootstrap_servers = 'kafka:9092'

topic = 'signals-topic'

# Consumer configuration
consumer_config = {
    'bootstrap.servers': bootstrap_servers,
    'group.id': 'consumer_group',
    'auto.offset.reset': 'earliest'
}

consumer = Consumer(consumer_config)

consumer.subscribe([topic])

try:
    while True:
        msg = consumer.poll(timeout=1.0)
        if msg is None:
            continue
        if msg.error():
            if msg.error().code() == KafkaError._PARTITION_EOF:
                print('%% %s [%d] reached end at offset %d\n' %
                      (msg.topic(), msg.partition(), msg.offset()))
            elif msg.error():
                print('Error: %s' % msg.error())
        else:
            print('Received message: %s' % msg.value().decode('utf-8'))

except KeyboardInterrupt:
    pass

finally:
    consumer.close()