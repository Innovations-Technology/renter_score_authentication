package com.iss.renterscore.authentication.model;

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
    @Column(name = "user_profile_id")
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String company;
    private Date dob;
    private String bio;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "property_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyRole propertyRole;


}
