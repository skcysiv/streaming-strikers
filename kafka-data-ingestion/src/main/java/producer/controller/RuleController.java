package producer.controller;

import com.common.model.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import producer.producer.KafkaAvroProducer;

import java.util.List;

@RestController
public class RuleController {
    @Autowired
    private KafkaAvroProducer producer;

    @PostMapping("/rules")
    public String sendRule(@RequestBody List<Rule> rules) {
        for(Rule rule : rules) {
            producer.sendRules(rule);
        }
        return rules.size() + " events published";
    }
}
