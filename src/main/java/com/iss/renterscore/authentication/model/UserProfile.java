package com.iss.renterscore.authentication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profile")
public class UserProfile extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_profile_seq")
    @SequenceGenerator(name = "user_profile_seq", allocationSize = 1)
    @JsonProperty(value = "user_profile_id")
    @Column(name = "user_profile_id")
    private Long id;

    @JsonProperty(value = "email")
    @Column(name = "email")
    private String email;

    @JsonProperty(value = "first_name")
    @Column(name = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    @Column(name = "last_name")
    private String lastName;

    @JsonProperty(value = "contact_number")
    @Column(name = "contact_number")
    private String contactNumber;

    @JsonProperty(value = "company")
    @Column(name = "company")
    private String company;

    @JsonProperty(value = "dob")
    @Column(name = "dob")
    private Date dob;

    @JsonProperty(value = "bio")
    @Column(name = "bio")
    private String bio;

    @JsonProperty(value = "gender")
    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @JsonProperty(value = "user")
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @JsonProperty(value = "property_role")
    @Column(name = "property_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyRole propertyRole;


}
