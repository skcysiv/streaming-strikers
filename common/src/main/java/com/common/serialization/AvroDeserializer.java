package com.common.serialization;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import com.common.model.EventUse;
import java.io.IOException;

public class AvroDeserializer implements KafkaRecordDeserializationSchema<EventUse> {

    private EventUse convertToEventUse(GenericRecord genericRecord){
        EventUse eventUse = new EventUse();
        eventUse.setId(genericRecord.get("id").toString());
        eventUse.setName(genericRecord.get("name").toString());
        eventUse.setTimestamp(genericRecord.get("timestamp").toString());
        eventUse.setPrice(genericRecord.get("price").toString());
        return eventUse;
    }

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> consumerRecord, Collector<EventUse> collector) throws IOException {
        byte[] value = consumerRecord.value();
        String avroSchemaString = "{\"type\":\"record\",\"name\":\"EventUse\",\"namespace\":\"com.common.model\",\"fields\":[{\"name\":\"timestamp\",\"type\":\"string\"},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"price\",\"type\":\"string\"},{\"name\":\"id\",\"type\":\"string\"}]}";
        Schema.Parser parser = new Schema.Parser();
        Schema avroSchema = parser.parse(avroSchemaString);

        // Deserialize Avro binary data
        DatumReader<GenericRecord> datumReader = new SpecificDatumReader<>(avroSchema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(value, null);
        GenericRecord genericRecord = datumReader.read(null, decoder);

        EventUse eventUse = convertToEventUse(genericRecord);
        System.out.println("Converted EventUse " + eventUse);
        collector.collect(eventUse);
    }

    @Override
    public TypeInformation<EventUse> getProducedType() {
        return TypeInformation.of(EventUse.class);
    }
}
