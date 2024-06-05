package producer.controller;

import com.common.model.Event;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import producer.producer.KafkaAvroProducer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class EventController {
    @Autowired
    private KafkaAvroProducer producer;

    private final Schema schema = Event.SCHEMA$;

    @PostMapping(value = "/events")
    public String sendMessage(@RequestBody List<String> events) throws IOException {
        DatumReader<GenericRecord> reader = new SpecificDatumReader<>(schema);
        for (String jsonEvent : events) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonEvent.getBytes());
            Decoder decoder = DecoderFactory.get().jsonDecoder(schema, inputStream);
            Event event = (Event) reader.read(null, decoder);
            System.out.println("Received event: " + event);
            producer.send(event);
        }
        return events.size() + " events published";
    }
}
