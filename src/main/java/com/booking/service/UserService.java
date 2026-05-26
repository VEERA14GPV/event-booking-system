package com.booking.service;

import com.booking.entity.User;

import com.booking.exception.ResourceNotFoundException;

import com.booking.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(
            UserRepository userRepository) {

        this.userRepository =
                userRepository;
    }

    /*
     * Get all users
     */
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    /*
     * Get user by ID
     */
    public User getUserById(
            Long userId) {

        return userRepository.findById(
                userId
        )
        .orElseThrow(() ->

                new ResourceNotFoundException(
                        "User not found"
                )
        );
    }

    /*
     * Get user by username
     */
    public User getUserByUsername(
            String username) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    /*
     * Delete user
     */
    public void deleteUser(
            Long userId) {

        User user =
                getUserById(userId);

        userRepository.delete(user);
    }
}