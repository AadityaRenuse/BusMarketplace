package com.busmarketplace.backend.repository;

import com.busmarketplace.backend.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Long> {
}