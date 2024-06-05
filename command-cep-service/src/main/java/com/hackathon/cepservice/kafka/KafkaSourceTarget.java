package com.hackathon.cepservice.kafka;

import com.common.model.Event;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.formats.avro.AvroDeserializationSchema;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.osgi.service.component.annotations.Component;

@Component
public class KafkaSourceTarget {

    public static KafkaSource<Event> start(String topic, String broker, String groupId) {
        AvroDeserializationSchema<Event> avroDeserializationSchema =
                AvroDeserializationSchema.forSpecific(Event.class);

        return KafkaSource.<Event>builder()
                .setBootstrapServers(broker).setTopics(topic)
                .setGroupId(groupId).setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(avroDeserializationSchema).build();
    }

    public static KafkaSource<String> startRulesSource(String topic, String broker, String groupId) {
        return KafkaSource.<String>builder()
                .setBootstrapServers(broker).setTopics(topic)
                .setGroupId(groupId).setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(StringDeserializer.class))
                .build();
    }
}
