package com.busmarketplace.backend.repository;

import com.busmarketplace.backend.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusRepository extends JpaRepository<Bus, Long> {

    List<Bus> findByAgencyId(Long agencyId);
}