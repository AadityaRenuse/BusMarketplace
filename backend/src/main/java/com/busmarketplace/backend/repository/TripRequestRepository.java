package com.busmarketplace.backend.repository;

import com.busmarketplace.backend.entity.TripRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRequestRepository extends JpaRepository<TripRequest, Long> {
}