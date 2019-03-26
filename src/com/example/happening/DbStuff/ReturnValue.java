package com.example.happening.DbStuff;

import java.io.Serializable;

/**
 * Return values from servers database connection
 */
public enum ReturnValue implements Serializable {
    SUCCESS(1), GENERAL_FAILURE (-1), NO_CONN_TO_DB(-2), NO_CONN_TO_SERVER(-3);

    private final int value;

    ReturnValue(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
