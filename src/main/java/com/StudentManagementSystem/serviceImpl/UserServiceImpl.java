package com.StudentManagementSystem.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.StudentManagementSystem.entity.User;
import com.StudentManagementSystem.repository.UserRepository;
import com.StudentManagementSystem.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repo;

    @Override
    public User saveUser(User user) {
        return repo.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }
}