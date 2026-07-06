package com.StudentManagementSystem.service;

import com.StudentManagementSystem.entity.User;

public interface UserService {
    User saveUser(User user);
    User findByEmail(String email);
}