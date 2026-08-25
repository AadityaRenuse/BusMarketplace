package com.busmarketplace.backend.controller;

import com.busmarketplace.backend.entity.Bid;
import com.busmarketplace.backend.entity.Booking;
import com.busmarketplace.backend.entity.TripRequest;
import com.busmarketplace.backend.repository.BidRepository;
import com.busmarketplace.backend.repository.BookingRepository;
import com.busmarketplace.backend.repository.TripRequestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidRepository bidRepository;
    private final TripRequestRepository tripRequestRepository;
    private final BookingRepository bookingRepository;

    public BidController(
            BidRepository bidRepository,
            TripRequestRepository tripRequestRepository,
            BookingRepository bookingRepository) {

        this.bidRepository = bidRepository;
        this.tripRequestRepository = tripRequestRepository;
        this.bookingRepository = bookingRepository;
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
    public Bid updateBid(
            @PathVariable Long id,
            @RequestBody Bid updatedBid) {

        Bid bid = bidRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        bid.setTripRequestId(updatedBid.getTripRequestId());
        bid.setAgencyId(updatedBid.getAgencyId());
        bid.setBusId(updatedBid.getBusId());
        bid.setAmount(updatedBid.getAmount());
        bid.setMessage(updatedBid.getMessage());
        bid.setStatus(updatedBid.getStatus());

        /*
         * ACCEPT BID
         */
        if ("ACCEPTED".equalsIgnoreCase(updatedBid.getStatus())) {

            TripRequest tripRequest = tripRequestRepository
                    .findById(bid.getTripRequestId())
                    .orElseThrow(() ->
                            new RuntimeException("Trip request not found"));

            /*
             * Make sure this trip has not already been booked.
             */
            if ("BOOKED".equalsIgnoreCase(tripRequest.getStatus())) {

                Bid existingAcceptedBid = bidRepository.findAll()
                        .stream()
                        .filter(existingBid ->
                                existingBid.getTripRequestId()
                                        .equals(bid.getTripRequestId()))
                        .filter(existingBid ->
                                "ACCEPTED".equalsIgnoreCase(
                                        existingBid.getStatus()))
                        .filter(existingBid ->
                                !existingBid.getBidId().equals(bid.getBidId()))
                        .findFirst()
                        .orElse(null);

                if (existingAcceptedBid != null) {
                    throw new RuntimeException(
                            "A bid has already been accepted for this trip");
                }
            }

            /*
             * Reject all other pending bids for this trip.
             */
            List<Bid> tripBids = bidRepository.findAll();

            for (Bid otherBid : tripBids) {

                if (otherBid.getTripRequestId()
                        .equals(bid.getTripRequestId())
                        && !otherBid.getBidId().equals(bid.getBidId())
                        && "PENDING".equalsIgnoreCase(
                                otherBid.getStatus())) {

                    otherBid.setStatus("REJECTED");
                    bidRepository.save(otherBid);
                }
            }

            /*
             * Mark the trip as BOOKED.
             */
            tripRequest.setStatus("BOOKED");
            tripRequestRepository.save(tripRequest);

            /*
             * Create the booking automatically.
             */
            Booking booking = new Booking();

            booking.setCustomerId(tripRequest.getCustomerId());
            booking.setAgencyId(bid.getAgencyId());
            booking.setBusId(bid.getBusId());
            booking.setTripRequestId(tripRequest.getTripRequestId());
            booking.setBidId(bid.getBidId());
            booking.setAmount(bid.getAmount());

            booking.setBookingType("REVERSE_BID");
            booking.setStatus("CONFIRMED");

            booking.setStartDate(tripRequest.getStartDate());
            booking.setEndDate(tripRequest.getEndDate());

            booking.setCreatedAt(LocalDateTime.now());

            bookingRepository.save(booking);
        }

        return bidRepository.save(bid);
    }

    @DeleteMapping("/{id}")
    public void deleteBid(@PathVariable Long id) {
        bidRepository.deleteById(id);
    }
}