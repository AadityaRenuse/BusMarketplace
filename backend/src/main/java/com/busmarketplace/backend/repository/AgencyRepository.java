package com.busmarketplace.backend.repository;

import com.busmarketplace.backend.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, Long> {

    Optional<Agency> findByUserId(Long userId);
}