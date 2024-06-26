package org.apache.flink.streaming.siddhi.router;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.siddhi.control.MetadataControlEvent;
import org.apache.flink.streaming.siddhi.control.OperationControlEvent;

import java.util.HashSet;
import java.util.Map;

public class ReduceDuplicateControlEventsFilter implements FilterFunction<Tuple2<StreamRoute, Object>> {
    private final HashSet<String> metaDataControlEventSet = new HashSet<>();
    private final HashSet<String> operationControlEventSet = new HashSet<>();

    @Override
    public boolean filter(Tuple2<StreamRoute, Object> streamRouteObjectTuple2) throws Exception {
        if(streamRouteObjectTuple2.f1 instanceof MetadataControlEvent){
            MetadataControlEvent metadataControlEvent = (MetadataControlEvent) streamRouteObjectTuple2.f1;
            for (Map.Entry<String, String> entry : metadataControlEvent.getAddedExecutionPlanMap().entrySet()) {
                if(!metaDataControlEventSet.contains(entry.getKey())){
                    metaDataControlEventSet.add(entry.getKey());
                    return true;
                }
                return false;
            }
        }
        if(streamRouteObjectTuple2.f1 instanceof OperationControlEvent){
            OperationControlEvent operationControlEvent = (OperationControlEvent) streamRouteObjectTuple2.f1;
            if(!operationControlEventSet.contains(operationControlEvent.getQueryId())){
                operationControlEventSet.add(operationControlEvent.getQueryId());
                return true;
            }
            return false;
        }
        return true;
    }
}
