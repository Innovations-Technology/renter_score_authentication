package com.iss.renterscore.authentication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public record UserDto(
        @JsonProperty(value = "user_id")
        Long id,
        @JsonProperty(value = "email")
        String email,
        @JsonProperty(value = "first_name")
        String firstName,
        @JsonProperty(value = "last_name")
        String lastName,
        @JsonProperty(value = "profile_image")
        String profileImage,
        @JsonProperty(value = "contact_number")
        String contactNumber,
        @JsonProperty(value = "company")
        String company,
        @JsonProperty(value = "property_role")
        PropertyRole propertyRole
) {
    public UserDto(Users user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getProfile().getFirstName(),
                user.getProfile().getLastName(),
                buildImageUrl(user.getProfile().getProfileImage()),
                user.getProfile().getContactNumber(),
                user.getProfile().getCompany(),
                user.getPropertyRole()
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
