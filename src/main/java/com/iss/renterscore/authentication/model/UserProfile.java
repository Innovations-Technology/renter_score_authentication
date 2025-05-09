package com.iss.renterscore.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    @Column(name = "profile_id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "company")
    private String company;

    @Column(name = "date_of_birth")
    private LocalDate dob;

    @Column(name = "biography")
    private String biography;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "property_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyRole propertyRole;

    @Column(name = "address")
    private String address;


}
