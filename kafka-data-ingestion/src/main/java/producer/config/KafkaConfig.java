package producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class KafkaConfig {

    @Value("${topic.name}")
    private String topicName;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    @Value("${spring.kafka.producer.keySerializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.valueSerializer}")
    private String valueSerializer;

    public static final Random RANDOM = new Random();

    public String getTopicName() {
        return topicName;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public String getSchemaRegistryUrl() {
        return schemaRegistryUrl;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    @Bean
    public NewTopic createTopic(){
        return new NewTopic(topicName, 1, (short) 1);
    }
}
