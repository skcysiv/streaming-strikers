package com.common.model;

import org.apache.flink.streaming.siddhi.control.ControlEvent;

import java.util.UUID;

public class Rule extends ControlEvent {
    private String query;
    private UUID ruleId;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public String toString(){
        return "{\"ruleId\": \"" + ruleId + "\", \"query\": \"" + query +"\"}";
    }
}
