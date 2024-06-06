FROM python:3.8
WORKDIR /send_events
COPY kafka_producer.py /send_events/
COPY kafka_consumer.py /send_events/
RUN pip3 install confluent-kafka
CMD ["/bin/bash"]