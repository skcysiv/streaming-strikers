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

package org.apache.flink.streaming.siddhi.utils;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.siddhi.SiddhiCEPConfig;
import org.apache.flink.streaming.siddhi.operator.SiddhiOperatorContext;
import org.apache.flink.streaming.siddhi.operator.SiddhiStreamOperator;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.siddhi.router.StreamRoute;

/**
 * Convert SiddhiCEPExecutionPlan to SiddhiCEP Operator and build output DataStream
 */
public class SiddhiStreamFactory {
    @SuppressWarnings("unchecked")
    public static <OUT> DataStream<OUT> createDataStream(SiddhiOperatorContext context, DataStream<Tuple2<StreamRoute, Object>> namedStream, SiddhiCEPConfig siddhiCEPConfig) {
        if(siddhiCEPConfig!= null && siddhiCEPConfig.isRuleBasedPartitioning()) {
            return namedStream.partitionCustom(siddhiCEPConfig.getHashPartitioner(), siddhiCEPConfig.getKeyBySelector()).transform(
                    context.getName(),
                    Types.TUPLE(TypeInformation.of(String.class), TypeInformation.of(Object.class)),
                    new SiddhiStreamOperator(context)).name("abstract-siddhi-operator").setParallelism(siddhiCEPConfig.getAbstractSiddhiOperatorParallelism());

        }
        return namedStream.transform(
                context.getName(),
                Types.TUPLE(TypeInformation.of(String.class), TypeInformation.of(Object.class)),
                new SiddhiStreamOperator(context)).name("abstract-siddhi-operator");
//                .setParallelism(siddhiCEPConfig.getAbstractSiddhiOperatorParallelism());
    }
}
