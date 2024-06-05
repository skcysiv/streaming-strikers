package producer.producer;

import com.common.model.Event;
import com.common.model.Rule;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import producer.config.KafkaConfig;
import java.util.Properties;

import static producer.config.KafkaConfig.RANDOM;


@Service
public class KafkaAvroProducer {

    @Autowired
    private KafkaConfig kafkaConfig;

    private Properties producerProps;

    @Bean
    public void setProducerProps(){
        producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getKeySerializer());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getValueSerializer());
        producerProps.put("schema.registry.url", kafkaConfig.getSchemaRegistryUrl());
    }

    public void send(Event event){
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getKeySerializer());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getValueSerializer());
        try (Producer<String, Event> producer = new KafkaProducer<>(producerProps)) {
            event.setEventId(RANDOM.nextInt());
            event.setEventTime(String.valueOf(System.currentTimeMillis()));
            producer.send(new ProducerRecord<>("events-topic", String.valueOf(event.getEventId()), event)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRules(Rule rule) {
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        try (Producer<String, String> producer = new KafkaProducer<>(producerProps)) {
            producer.send(new ProducerRecord<>("rules-topic", String.valueOf(rule.getRuleId()), rule.toString())).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
