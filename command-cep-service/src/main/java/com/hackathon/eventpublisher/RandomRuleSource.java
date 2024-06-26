package com.hackathon.eventpublisher;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.siddhi.control.ControlEvent;
import org.apache.flink.streaming.siddhi.control.MetadataControlEvent;
import org.apache.flink.streaming.siddhi.control.OperationControlEvent;


public class RandomRuleSource implements SourceFunction<ControlEvent> {

    @Override
    public void run(SourceContext<ControlEvent> sourceContext) throws Exception {
        Thread.sleep(1000);
        String id = "bd80f0dc-d313-4703-a304-c17c75538995";
        MetadataControlEvent controlEvent = MetadataControlEvent.builder().addExecutionPlan(id, "from every s1 = awsStream[dst_ip4 == '1.2.3.4']<1> "
                + " -> s2 = awsStream[dst_ip4 == '1.2.3.4']<1> within 5000 second "
                + "select s1.dst_ip4 as dst_ip4_1, s2.dst_ip4 as dst_ip4_2, s1.access_check_results as access_check_results_1, s2.access_check_results as access_check_results_2, s1.event_id as event_id_1, s2.event_id as event_id_2, s1.data_source as data_source_1, s2.data_source as data_source_2 "
                + "insert into outputStream").build();
        sourceContext.collect(controlEvent);
        OperationControlEvent operationControlEvent = OperationControlEvent.enableQuery(id);
        sourceContext.collect(operationControlEvent);
    }

    @Override
    public void cancel() {

    }
}
