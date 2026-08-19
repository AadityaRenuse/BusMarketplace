package com.busmarketplace.backend.repository;

import com.busmarketplace.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}