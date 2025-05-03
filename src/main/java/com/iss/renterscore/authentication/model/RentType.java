package com.iss.renterscore.authentication.model;

import lombok.Getter;

@Getter
public enum RentType {
    MONTHLY ("Monthly"), YEARLY ("Yearly");

    public final String value;

    RentType(String name) {
        this.value = name;
    }
}
