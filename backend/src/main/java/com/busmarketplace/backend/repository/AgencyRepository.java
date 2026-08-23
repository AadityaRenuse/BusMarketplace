package com.busmarketplace.backend.repository;

import com.busmarketplace.backend.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
}