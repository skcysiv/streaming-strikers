package com.hackathon.eventpublisher;

import org.apache.flink.streaming.siddhi.control.MetadataControlEvent;

public class Rule extends MetadataControlEvent {
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
