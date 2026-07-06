package com.StudentManagementSystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.StudentManagementSystem.entity.User;
import com.StudentManagementSystem.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with email: " + email);
        }

        // Default to ADMIN if role is null or empty
        String role = (user.getRole() == null || user.getRole().isBlank()) ? "ADMIN" : user.getRole();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(role)
                .build();
    }
}