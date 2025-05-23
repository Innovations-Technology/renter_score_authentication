package com.iss.renterscore.authentication.model;

import lombok.Getter;

@Getter
public enum PropertyType {
    CONDO("Condo"), EXECUTIVE_CONDO("Executive Condo"), HDB("HDB"), LANDED("Landed");

    public final String value;

    PropertyType(String name) {
        this.value = name;
    }
}
