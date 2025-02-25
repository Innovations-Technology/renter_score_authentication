package com.iss.renterscore.authentication.repos;

import com.iss.renterscore.authentication.model.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepo extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u WHERE u.email = :email")
    Users existsByEmail(@Param("email") String email);

    @Query("SELECT u FROM Users u WHERE u.verificationToken = :token")
    Optional<Users> findByEmailToken(@Param("token") String token);

}
