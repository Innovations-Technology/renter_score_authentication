package com.iss.renterscore.authentication.model;

import lombok.Getter;

@Getter
public enum Regions {
    SINGAPORE ("Singapore"), CENTRAL_SINGAPORE ("Central Singapore"), NORTH_EAST ("North East"),
    NORTH_WEST ("North West"), SOUTH_EAST ("South East"), SOUTH_WEST ("South West");

    public final String name;


    Regions(String name) {
        this.name = name;
    }
}
