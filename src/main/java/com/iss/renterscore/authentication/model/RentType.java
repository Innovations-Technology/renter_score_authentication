package com.iss.renterscore.authentication.model;

import lombok.Getter;

@Getter
public enum RentType {
    MONTHLY ("Monthly"), YEARLY ("Yearly");

    public final String name;

    RentType(String name) {
        this.name = name;
    }
}
