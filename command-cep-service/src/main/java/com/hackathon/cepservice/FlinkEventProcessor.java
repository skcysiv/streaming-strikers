package com.hackathon.cepservice;

import com.common.model.Event;
import com.common.model.EventFields;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.eventpublisher.RandomEventSource;
import com.hackathon.eventpublisher.RandomRuleSource;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.siddhi.SiddhiCEP;
import org.apache.flink.streaming.siddhi.SiddhiCEPConfig;
import org.apache.flink.streaming.siddhi.SiddhiStream;
import org.apache.flink.streaming.siddhi.control.ControlEvent;
import org.apache.flink.util.Collector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FlinkEventProcessor {
    private static final Logger logger = LogManager.getLogger(Main.class);

    @Value("${topic.producer.name}")
    private static String topicProducerName = "signals-topic";

    @Value("${topic.consumer.name}")
    private static String topicConsumerName = "events-topic";
    private static String ruleTopicConsumerName = "rules-topic";

    @Value("${spring.kafka.bootstrap-servers}")
    private static String kafkaBroker = "kafka:9092";

    @Value("${spring.kafka.consumer.group-id}")
    private static String groupId = "flink-cep";

    private static final String[] sourceFilters = {"aws", "qualys", "trendmicro", "graph", "box"};

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private static final List<DataStream<Event>> eventStreams = new ArrayList<>();

    private static DataStream<Map<String, Object>> initializeSiddhiCEP(String[] stringArray, DataStream<ControlEvent> ruleDataStream, SiddhiCEPConfig siddhiCEPConfig) {
        SiddhiStream.SingleSiddhiStream<Event> streams = SiddhiCEP
                .define(sourceFilters[0]+"Stream", eventStreams.get(0), stringArray);
        for(int i=1; i < sourceFilters.length; i++){
            streams.union(sourceFilters[i]+"Stream", eventStreams.get(i), stringArray);
        }
        return streams.cql(ruleDataStream, siddhiCEPConfig).returnAsMap("outputStream");
    }

    private static void registerStreams(DataStream<Event> input1){
        int numStreams = sourceFilters.length;
        DataStream<Event>[] inputStreams = new DataStream[numStreams];

        for (int i = 0; i < numStreams; i++) {
            final int index = i;
            inputStreams[i] = input1.filter(event -> event.getDataSource().equals(sourceFilters[index])).map(event -> {
                System.out.println("received-event-"+index+event);
                return event;
            });
            eventStreams.add(inputStreams[i]);
        }
    }

    public static void process(String[] args) throws Exception {
        int parallelism = 4;
        String jobName = "Streaming-Rule-Engine-1";
        boolean ruleBasedPartitioning = false;

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStream<ControlEvent> ruleDataStream = null;
        logger.info("Received config: topicProducerName {}, topicConsumerName {}, kafkaBroker {}, groupId {}", topicProducerName, topicConsumerName, kafkaBroker, groupId);

        WatermarkStrategy<Event> watermarkStrategy = WatermarkStrategy
                .<Event>forMonotonousTimestamps()
                .withTimestampAssigner((event, timestamp) -> Long.parseLong(event.getEventTime()));


        DataStream<Event> input1 = env.addSource(new RandomEventSource(Integer.MAX_VALUE).closeDelay(1500), "input1").name("events-consumer-1");
            ruleDataStream = env.addSource(new RandomRuleSource(), "ruleDataStream").name("rules-consumer");


        registerStreams(input1);

        SiddhiCEPConfig siddhiCEPConfig = new SiddhiCEPConfig(parallelism, parallelism, parallelism, parallelism, ruleBasedPartitioning);

        List<String> fields = EventFields.getFields();

        String[] stringArray = fields.toArray(new String[0]);

        DataStream<Map<String, Object>> output = initializeSiddhiCEP(stringArray, ruleDataStream, siddhiCEPConfig);
        DataStream<String> signal = output.flatMap(new FlatMapFunction<Map<String, Object>, String>() {

            @Override
            public void flatMap(Map<String, Object> stringObjectMap, Collector<String> collector) throws Exception {
                collector.collect(stringObjectMap.toString());
            }
        }).setParallelism(1);

        signal.print();

        env.setMaxParallelism(8);
        env.execute(jobName);
    }
}
