/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.streaming.siddhi.operator;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.streaming.runtime.streamrecord.StreamElement;
import org.apache.flink.streaming.runtime.streamrecord.StreamElementSerializer;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.siddhi.control.ControlEvent;
import org.apache.flink.streaming.siddhi.router.StreamRoute;
import org.apache.flink.streaming.siddhi.schema.StreamSchema;
import org.apache.flink.streaming.siddhi.utils.SiddhiTypeFactory;

import java.io.IOException;
import java.util.PriorityQueue;

/**
 * Wrap input event in generic type of <code>IN</code> as Tuple2<StreamRoute,IN>
 */
public class SiddhiStreamOperator<IN, OUT> extends AbstractSiddhiOperator<Tuple2<StreamRoute, IN>, OUT> {

    public SiddhiStreamOperator(SiddhiOperatorContext siddhiPlan) {
        super(siddhiPlan);
    }

    @Override
    protected StreamElementSerializer<Tuple2<StreamRoute, IN>> createStreamRecordSerializer(StreamSchema streamSchema, ExecutionConfig executionConfig) {
        TypeInformation<Tuple2<StreamRoute, IN>> tuple2TypeInformation = SiddhiTypeFactory.getStreamTupleTypeInformation((TypeInformation<IN>) streamSchema.getTypeInfo());
        return new StreamElementSerializer<>(tuple2TypeInformation.createSerializer(executionConfig));
    }

    @Override
    protected void processEvent(String streamId, StreamSchema<Tuple2<StreamRoute, IN>> schema, Tuple2<StreamRoute, IN> value, long timestamp) throws InterruptedException {
        send(value.f0, getSiddhiPlan().getInputStreamSchema(value.f0.getInputStreamId()).getStreamSerializer().getRow(value.f1), timestamp);
    }

    @Override
    public String getStreamId(Tuple2<StreamRoute, IN> record) {
        return record.f0.getInputStreamId();
    }

    @Override
    public boolean isControlStream(Tuple2<StreamRoute, IN> record) {
        return ControlEvent.DEFAULT_INTERNAL_CONTROL_STREAM.equals(record.f0.getInputStreamId());
    }

    @Override
    public ControlEvent getControlEvent(Tuple2<StreamRoute, IN> record) {
        return (ControlEvent) record.f1;
    }

    @Override
    protected void snapshotQueueState(PriorityQueue<StreamRecord<Tuple2<StreamRoute, IN>>> queue, DataOutputView dataOutputView) throws IOException {
        dataOutputView.writeInt(queue.size());
        for (StreamRecord<Tuple2<StreamRoute, IN>> record : queue) {
            String streamId = record.getValue().f0.getInputStreamId();
            dataOutputView.writeUTF(streamId);
            this.getStreamRecordSerializer(streamId).serialize(record, dataOutputView);
        }
    }

    @Override
    protected PriorityQueue<StreamRecord<Tuple2<StreamRoute, IN>>> restoreQueueState(DataInputView dataInputView) throws IOException {
        int sizeOfQueue = dataInputView.readInt();
        PriorityQueue<StreamRecord<Tuple2<StreamRoute, IN>>> priorityQueue = new PriorityQueue<>(sizeOfQueue + 1);
        for (int i = 0; i < sizeOfQueue; i++) {
            String streamId = dataInputView.readUTF();
            StreamElement streamElement = getStreamRecordSerializer(streamId).deserialize(dataInputView);
            priorityQueue.offer(streamElement.<Tuple2<StreamRoute, IN>>asRecord());
        }
        return priorityQueue;
    }
}
