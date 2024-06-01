package org.eaetirk.demo.elastic.query.service.config;

public enum QueryType {
    KAFKA_STATE_STORE("KAFKA_STATE_STORE"),
    ANALYTICS_DATABASE("ANALYTICS_DATABASE");

    private final String type;

    QueryType(String analyticsDatabase) {
        this.type=analyticsDatabase;
    }

    public String getType() {
        return type;
    }
}
