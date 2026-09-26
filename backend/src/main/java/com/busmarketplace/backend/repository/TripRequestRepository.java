package com.busmarketplace.backend.repository;

import com.busmarketplace.backend.entity.TripRequest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRequestRepository extends JpaRepository<TripRequest, Long> {

    List<TripRequest> findByCustomerId(Long customerId);
}