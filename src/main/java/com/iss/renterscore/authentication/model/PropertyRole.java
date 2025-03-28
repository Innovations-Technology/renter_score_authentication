package com.iss.renterscore.authentication.model;

import lombok.Getter;

@Getter
public enum PropertyRole {
        ROLE_TENANT("TENANT"), ROLE_AGENT("AGENT"), ROLE_LANDLORD("LANDLORD");

        public final String name;

        PropertyRole(String tenant) {
                this.name = tenant;
        }

}
