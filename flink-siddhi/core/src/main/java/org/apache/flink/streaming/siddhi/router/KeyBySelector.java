package org.apache.flink.streaming.siddhi.router;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;

public class KeyBySelector implements KeySelector<Tuple2<StreamRoute, Object>, Long> {

    private int partitionNumber = -1;

    public KeyBySelector(int partitionNumber) {
        this.partitionNumber = partitionNumber;
    }

    @Override
    public Long getKey(Tuple2<StreamRoute, Object> streamRouteObjectTuple2) throws Exception {
        if(partitionNumber != -1) {
            return ((partitionNumber * streamRouteObjectTuple2.f0.getPartitionKey()) + streamRouteObjectTuple2.f0.getPartitionKey());
        }
        return streamRouteObjectTuple2.f0.getPartitionKey();
    }
}
