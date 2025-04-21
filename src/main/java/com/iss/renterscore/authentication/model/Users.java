package com.iss.renterscore.authentication.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class Users extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", allocationSize = 1)

    @JsonProperty(value = "user_id")
    @Column(name = "user_id")
    private Long id;

    @JsonProperty(value = "email")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @JsonProperty(value = "user_profile")
    @JsonManagedReference
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;

    @JsonProperty(value = "user_role")
    @Column(name = "user_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @JsonProperty(value = "property_role")
    @Column(name = "property_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyRole propertyRole;

    @JsonProperty(value = "verification_token")
    @JsonIgnore
    @Column(name = "verification_token", nullable = false)
    private String verificationToken;

    @JsonProperty(value = "email_status")
    @Column(name = "email_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmailVerificationStatus emailVerificationStatus;

    @JsonProperty(value = "expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @JsonProperty(value = "account_status")
    @Column(name = "account_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;


    public Users(Users user) {
        id = user.getId();
        email = user.getEmail();
        password = user.getPassword();
        profile = user.getProfile();
        userRole = user.getUserRole();
        propertyRole = user.getPropertyRole();
        emailVerificationStatus = user.getEmailVerificationStatus();
        verificationToken = user.getVerificationToken();
        accountStatus = user.getAccountStatus();
        expiryDate = user.getExpiryDate();
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
        profile.setUser(this);
    }
}
