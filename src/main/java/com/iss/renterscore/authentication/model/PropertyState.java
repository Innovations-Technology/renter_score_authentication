package com.iss.renterscore.authentication.model;

public enum PropertyState {

    AVAILABLE("Available"), OCCUPIED("Occupied"), SUSPENDED("Suspended");

    public final String value;

    PropertyState(String name) {
        this.value = name;
    }
}
