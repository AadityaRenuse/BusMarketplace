package com.busmarketplace.backend.repository;

import com.busmarketplace.backend.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByTripRequestId(Long tripRequestId);

    List<Bid> findByAgencyId(Long agencyId);
}