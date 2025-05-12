package com.iss.renterscore.authentication;

import com.iss.renterscore.authentication.model.EmailVerificationStatus;
import com.iss.renterscore.authentication.model.UserRole;
import com.iss.renterscore.authentication.model.Users;

public class MockUserFactory {
    public static Users createDefaultUser() {
        return createUser(1L, "john.doe@example.com", "password123", UserRole.ROLE_USER, EmailVerificationStatus.STATUS_VERIFIED);
    }

    public static Users createUser(Long id, String email, String password) {
        return createUser(id, email, password, UserRole.ROLE_USER, EmailVerificationStatus.STATUS_VERIFIED);
    }

    public static Users createUser(Long id, String email, String password, UserRole role, EmailVerificationStatus verificationStatus) {
        Users user = new Users();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setUserRole(role);
        user.setEmailVerificationStatus(verificationStatus);
        return user;
    }
}
