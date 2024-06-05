package org.apache.flink.streaming.siddhi;

import org.apache.flink.streaming.siddhi.router.HashPartitioner;

import java.io.Serializable;

public class SiddhiCEPConfig implements Serializable {
    private int addRouteOperatorParallelism = 1;

    private int abstractSiddhiOperatorParallelism = 1;

    private int controlEventFilterParallelism = 1;

    private int executionPlanAddParallelism = 1;

    private boolean isRuleBasedPartitioning = false;

    private HashPartitioner hashPartitioner;

    public SiddhiCEPConfig(int addRouteOperatorParallelism, int abstractSiddhiOperatorParallelism, int controlEventFilterParallelism, int executionPlanAddParallelism, boolean isRuleBasedPartitioning) {
        this.addRouteOperatorParallelism = addRouteOperatorParallelism;
        this.abstractSiddhiOperatorParallelism = abstractSiddhiOperatorParallelism;
        this.isRuleBasedPartitioning = isRuleBasedPartitioning;
        this.controlEventFilterParallelism = controlEventFilterParallelism;
        this.executionPlanAddParallelism = executionPlanAddParallelism;
    }

    public int getAddRouteOperatorParallelism() {
        return addRouteOperatorParallelism;
    }

    public void setAddRouteOperatorParallelism(int addRouteOperatorParallelism) {
        this.addRouteOperatorParallelism = addRouteOperatorParallelism;
    }

    public int getAbstractSiddhiOperatorParallelism() {
        return abstractSiddhiOperatorParallelism;
    }

    public void setAbstractSiddhiOperatorParallelism(int abstractSiddhiOperatorParallelism) {
        this.abstractSiddhiOperatorParallelism = abstractSiddhiOperatorParallelism;
    }

    public boolean isRuleBasedPartitioning() {
        return isRuleBasedPartitioning;
    }

    public void setRuleBasedPartitioning(boolean ruleBasedPartitioning) {
        isRuleBasedPartitioning = ruleBasedPartitioning;
    }

    public HashPartitioner getHashPartitioner() {
        return hashPartitioner;
    }

    public void setHashPartitioner(HashPartitioner hashPartitioner) {
        this.hashPartitioner = hashPartitioner;
    }

    public int getControlEventFilterParallelism() {
        return controlEventFilterParallelism;
    }

    public void setControlEventFilterParallelism(int controlEventFilterParallelism) {
        this.controlEventFilterParallelism = controlEventFilterParallelism;
    }

    public int getExecutionPlanAddParallelism() {
        return executionPlanAddParallelism;
    }

    public void setExecutionPlanAddParallelism(int executionPlanAddParallelism) {
        this.executionPlanAddParallelism = executionPlanAddParallelism;
    }
}
