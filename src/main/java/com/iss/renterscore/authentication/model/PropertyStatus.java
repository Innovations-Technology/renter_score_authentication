package com.iss.renterscore.authentication.model;

import lombok.Getter;

@Getter
public enum PropertyStatus {
    PARTIALLY_FINISHED ("Partially Finished"), FULLY_FINISHED ("Fully Finished");

    public final String name;


    PropertyStatus(String name) {
        this.name = name;
    }
}
