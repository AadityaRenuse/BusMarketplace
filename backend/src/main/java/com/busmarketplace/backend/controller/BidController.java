package com.busmarketplace.backend.controller;

import com.busmarketplace.backend.entity.TripRequest;
import com.busmarketplace.backend.repository.TripRequestRepository;
import com.busmarketplace.backend.entity.Bid;
import com.busmarketplace.backend.repository.BidRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidRepository bidRepository;
    private final TripRequestRepository tripRequestRepository;

    public BidController(BidRepository bidRepository,
                    TripRequestRepository tripRequestRepository) {
        this.bidRepository = bidRepository;
        this.tripRequestRepository = tripRequestRepository;
}
    @GetMapping
    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bid> getBidById(@PathVariable Long id) {
        return bidRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    

    @PostMapping
    public Bid createBid(@RequestBody Bid bid) {
        bid.setCreatedAt(LocalDateTime.now());

        if (bid.getStatus() == null || bid.getStatus().isBlank()) {
            bid.setStatus("PENDING");
        }

        return bidRepository.save(bid);
    }

@PutMapping("/{id}")
public Bid updateBid(@PathVariable Long id, @RequestBody Bid updatedBid) {

    Bid bid = bidRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Bid not found"));

    bid.setTripRequestId(updatedBid.getTripRequestId());
    bid.setAgencyId(updatedBid.getAgencyId());
    bid.setBusId(updatedBid.getBusId());
    bid.setAmount(updatedBid.getAmount());
    bid.setMessage(updatedBid.getMessage());
    bid.setStatus(updatedBid.getStatus());

    // If bid is accepted, book the related trip request
    if ("ACCEPTED".equalsIgnoreCase(updatedBid.getStatus())) {

        TripRequest tripRequest = tripRequestRepository
                .findById(updatedBid.getTripRequestId())
                .orElseThrow(() -> new RuntimeException("Trip request not found"));

        tripRequest.setStatus("BOOKED");

        tripRequestRepository.save(tripRequest);
    }

    return bidRepository.save(bid);
}

    @DeleteMapping("/{id}")
    public void deleteBid(@PathVariable Long id) {
        bidRepository.deleteById(id);
    }
}