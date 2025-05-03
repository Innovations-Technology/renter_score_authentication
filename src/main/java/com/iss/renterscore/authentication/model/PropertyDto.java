package com.iss.renterscore.authentication.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record PropertyDto(
        @JsonProperty(value = "property_id")
        Long id,
        @JsonProperty(value = "title")
        String title,
        @JsonProperty(value = "description")
        String description,
        @JsonProperty(value = "hero_image")
        String heroImage,
        @JsonProperty(value = "images")
        List<String> images,
        @JsonProperty(value = "property_type")
        PropertyType propertyType,
        @JsonProperty(value = "amenities")
        String amenities,
        @JsonProperty(value = "bedrooms")
        Integer bedrooms,
        @JsonProperty(value = "bathrooms")
        Integer bathrooms,
        @JsonProperty(value = "address")
        Address address,
        @JsonProperty(value = "available_date")
        String availableDate,
        @JsonProperty(value = "property_status")
        PropertyStatus propertyStatus,
        @JsonProperty(value = "price")
        Integer price,
        @JsonProperty(value = "currency")
        String currency,
        @JsonProperty(value = "size")
        String size,
        @JsonProperty(value = "rent_type")
        RentType rentType,
        @JsonProperty(value = "user")
        UserDto user,
        @JsonProperty(value = "property_state")
        PropertyState propertyState,
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

    public PropertyDto(Property property) {
        this(
                property.getId(),
                property.getTitle(),
                property.getDescription(),
                buildImageUrl(property.getHeroImage()),
                property.getImages().stream()
                        .map(img -> buildImageUrl(img.getImage()))
                        .filter(Objects::nonNull)
                        .toList(),
                property.getPropertyType(),
                property.getAmenities(),
                property.getBedrooms(),
                property.getBathrooms(),
                property.getAddress(),
                property.getAvailableDate(),
                property.getPropertyStatus(),
                property.getPrice(),
                property.getCurrency(),
                property.getSize(),
                property.getRentType(),
                new UserDto(property.getUser()),
                property.getPropertyState(),
                property.getCreatedDate(),
                property.getModifiedDate(),
                property.getCreatedUser(),
                property.getModifiedUser()
        );

    }

    private static String buildImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) return null;

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/")
                .path(imagePath)
                .toUriString();
    }

}
