package com.iss.renterscore.authentication.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RentPropertyDto (
    @JsonProperty(value = "rent_id")
    Long id,
    @JsonProperty(value = "tenant")
    UserDto tenant,
    @JsonProperty(value = "property")
    PropertyDto property,
    @JsonProperty(value = "owner")
    UserDto owner,
    @JsonProperty(value = "rent_status")
    RentStatus rentStatus,
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "UTC")
    @JsonProperty(value = "start_date")
    LocalDate startDate,
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "UTC")
    @JsonProperty(value = "end_date")
    LocalDate endDate,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    @JsonProperty(value = "created_date")
    LocalDateTime createdDate,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    @JsonProperty(value = "modified_date")
    LocalDateTime modifiedDate,
    @JsonProperty(value = "created_user")
    Long createdUser,
    @JsonProperty(value = "modified_user")
    Long modifiedUser

) {
    public RentPropertyDto(RentProperty rentProperty) {
        this (
                rentProperty.getId(),
                new UserDto(rentProperty.getTenant()),
                new PropertyDto(rentProperty.getProperty(), false),
                new UserDto(rentProperty.getOwner()),
                rentProperty.getRentStatus(),
                rentProperty.getStartDate(),
                rentProperty.getEndDate(),
                rentProperty.getCreatedDate(),
                rentProperty.getModifiedDate(),
                rentProperty.getCreatedUser(),
                rentProperty.getModifiedUser()
        );
    }

}
