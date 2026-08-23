package com.busmarketplace.backend.repository;

import com.busmarketplace.backend.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
}