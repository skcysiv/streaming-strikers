#!/bin/sh

docker pull shreddersk/streaming-strikers-cep-flink:latest
docker pull shreddersk/streaming-strikers-custom-kafka:latest
docker pull zookeeper:3.7.0
docker pull shreddersk/streaming-strikers-kafka-data-ingestion:latest
docker pull shreddersk/streaming-strikers-cp-schema-registry:latest
