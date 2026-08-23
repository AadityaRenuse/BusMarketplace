package com.busmarketplace.backend.controller;

import com.busmarketplace.backend.entity.User;
import com.busmarketplace.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
    @PutMapping("/{id}")
public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
    User existingUser = userRepository.findById(id).orElse(null);

    if (existingUser == null) {
        return null;
    }

    existingUser.setName(updatedUser.getName());
    existingUser.setEmail(updatedUser.getEmail());
    existingUser.setPhone(updatedUser.getPhone());
    existingUser.setRole(updatedUser.getRole());
    existingUser.setPasswordHash(updatedUser.getPasswordHash());

    return userRepository.save(existingUser);
}
    @DeleteMapping("/{id}")
public void deleteUser(@PathVariable Long id) {
    userRepository.deleteById(id);
}
}