package com.flextalk.we.cmmn.util;

public enum DataSourceType {
    SLAVE("SLAVE"),
    MASTER("MASTER");

    private final String type;

    DataSourceType(final String type) {
        this.type = type;
    }
}
