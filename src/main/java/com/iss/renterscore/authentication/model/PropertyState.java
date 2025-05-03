package com.iss.renterscore.authentication.model;

public enum PropertyState {

    AVAILABLE("Available"), OCCUPIED("Occupied"), SUSPENDED("Suspended");

    public final String name;

    PropertyState(String name) {
        this.name = name;
    }
}
