package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.model.CustomUserDetails;
import com.iss.renterscore.authentication.model.Users;
import com.iss.renterscore.authentication.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Users> users = userRepo.findByEmail(email);

        return users.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("Couldn't find a matching email for " + email));
    }

    public UserDetails loadUserById(Long id) {
        Optional<Users> users = userRepo.findById(id);
        return users.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("Couldn't find a matching email for " + id));
    }
}
