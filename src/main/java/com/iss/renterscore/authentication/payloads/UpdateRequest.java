package com.iss.renterscore.authentication.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iss.renterscore.authentication.model.Gender;
import com.iss.renterscore.authentication.model.PropertyRole;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateRequest {
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("email")
    private String email;
    @JsonProperty("contact_number")
    private String contactNumber;
    @JsonProperty("company")
    private String company;
    @JsonProperty("date_of_birth")
    private String dob;
    @JsonProperty("biography")
    private String biography;
    @JsonProperty("gender")
    private Gender gender;
    @JsonProperty("property_role")
    private PropertyRole propertyRole;
    @JsonProperty("address")
    private String address;
    @JsonProperty("profile_image")
    private String profileImage;
}
