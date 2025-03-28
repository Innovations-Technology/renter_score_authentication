package com.iss.renterscore.authentication.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    @JsonProperty(value = "profile_id")
    @Column(name = "profile_id")
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

    @JsonProperty(value = "profile_image")
    @Column(name = "profile_image")
    private String profileImage;

    @JsonProperty(value = "contact_number")
    @Column(name = "contact_number")
    private String contactNumber;

    @JsonProperty(value = "company")
    @Column(name = "company")
    private String company;

    @JsonProperty(value = "date_of_birth")
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "UTC")
    @Column(name = "date_of_birth")
    private LocalDate dob;

    @JsonProperty(value = "biography")
    @Column(name = "biography")
    private String biography;

    @JsonProperty(value = "gender")
    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @JsonProperty(value = "user")
    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @JsonProperty(value = "property_role")
    @Column(name = "property_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyRole propertyRole;

    @JsonProperty(value = "address")
    @Column(name = "address")
    private String address;


}
