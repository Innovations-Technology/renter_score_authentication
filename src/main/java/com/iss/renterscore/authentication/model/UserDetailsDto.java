package com.iss.renterscore.authentication.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.iss.renterscore.authentication.model.UserDto.buildImageUrl;

public record UserDetailsDto(
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
        @JsonProperty(value = "user_role")
        UserRole userRole,
        @JsonProperty(value = "property_role")
        PropertyRole propertyRole,
        @JsonProperty(value = "email_status")
        EmailVerificationStatus emailVerificationStatus,
        @JsonProperty(value = "account_status")
        AccountStatus accountStatus,
        @JsonProperty(value = "profile_id")
        Long profileId,
        @JsonFormat(pattern = "dd/MM/yyyy", timezone = "UTC")
        @JsonProperty(value = "date_of_birth")
        LocalDate dob,
        @JsonProperty(value = "biography")
        String biography,
        @JsonProperty(value = "gender")
        Gender gender,
        @JsonProperty(value = "address")
        String address,
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
    public UserDetailsDto(Users user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getProfile().getFirstName(),
                user.getProfile().getLastName(),
                buildImageUrl(user.getProfile().getProfileImage()),
                user.getProfile().getContactNumber(),
                user.getProfile().getCompany(),
                user.getUserRole(),
                user.getPropertyRole(),
                user.getEmailVerificationStatus(),
                user.getAccountStatus(),
                user.getProfile().getId(),
                user.getProfile().getDob(),
                user.getProfile().getBiography(),
                user.getProfile().getGender(),
                user.getProfile().getAddress(),
                user.getCreatedDate(),
                user.getProfile().getModifiedDate(),
                user.getCreatedUser(),
                user.getProfile().getModifiedUser()
        );
    }
}
