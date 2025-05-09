package com.iss.renterscore.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

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

    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;

    @Column(name = "user_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "property_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyRole propertyRole;

    @Column(name = "verification_token", nullable = false)
    private String verificationToken;

    @Column(name = "email_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmailVerificationStatus emailVerificationStatus;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

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
