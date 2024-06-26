package producer;

import com.common.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import producer.producer.KafkaAvroProducer;

import static producer.config.KafkaConfig.RANDOM;

@SpringBootApplication
public class KafkaDataIngestionApplication {
	@Autowired
	private static KafkaAvroProducer producer;

		public static void generateRandomEvents(){
		Event event = new Event();
		event.setDstIp4("1.2.3.4");
		event.setDataSource("aws");
		while (true){
			event.setEventId(RANDOM.nextInt());
			producer.send(event);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(KafkaDataIngestionApplication.class, args);
	}
}
