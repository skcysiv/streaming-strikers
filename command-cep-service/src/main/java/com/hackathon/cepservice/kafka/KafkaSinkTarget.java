package com.hackathon.cepservice.kafka;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.osgi.service.component.annotations.Component;

@Component
public class KafkaSinkTarget {

    public static KafkaSink<String> getKafkaSink(String topic, String broker){
        KafkaSink<String> sink = KafkaSink.<String>builder()
                    .setBootstrapServers(broker)
                    .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                            .setTopic(topic)
                            .setValueSerializationSchema(new SimpleStringSchema())
                            .build()
                    )
                    .build();
        return sink;
    }
}
