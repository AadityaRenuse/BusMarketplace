
package com.busmarketplace.backend.controller;

import com.busmarketplace.backend.entity.Bid;
import com.busmarketplace.backend.entity.Booking;
import com.busmarketplace.backend.entity.TripRequest;
import com.busmarketplace.backend.entity.User;
import com.busmarketplace.backend.entity.Agency;

import com.busmarketplace.backend.repository.BidRepository;
import com.busmarketplace.backend.repository.BookingRepository;
import com.busmarketplace.backend.repository.TripRequestRepository;
import com.busmarketplace.backend.repository.UserRepository;
import com.busmarketplace.backend.repository.AgencyRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
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
    private final UserRepository userRepository;
    private final AgencyRepository agencyRepository;

    public BidController(
            BidRepository bidRepository,
            TripRequestRepository tripRequestRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository,
            AgencyRepository agencyRepository) {

        this.bidRepository = bidRepository;
        this.tripRequestRepository = tripRequestRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.agencyRepository = agencyRepository;
    }

    // GET BIDS OF LOGGED-IN AGENCY
    @GetMapping("/my")
    public ResponseEntity<?> getMyBids(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not found");
        }

        if (!"AGENCY".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only agencies can view agency bids");
        }

        Agency agency = agencyRepository
                .findByUserId(user.getUserId())
                .orElse(null);

        if (agency == null) {
            return ResponseEntity.status(404)
                    .body("Agency profile not found");
        }

        return ResponseEntity.ok(
                bidRepository.findByAgencyId(agency.getAgencyId())
        );
    }

    // GET BIDS FOR A CUSTOMER'S OWN TRIP
    @GetMapping("/my-trip/{tripRequestId}")
    public ResponseEntity<?> getMyTripBids(
            @PathVariable Long tripRequestId,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not found");
        }

        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only customers can view trip bids");
        }

        TripRequest tripRequest = tripRequestRepository
                .findById(tripRequestId)
                .orElse(null);

        if (tripRequest == null) {
            return ResponseEntity.notFound().build();
        }

        if (tripRequest.getCustomerId() == null ||
                !tripRequest.getCustomerId().equals(user.getUserId())) {

            return ResponseEntity.status(403)
                    .body("You are not allowed to view bids for this trip");
        }

        return ResponseEntity.ok(
                bidRepository.findByTripRequestId(tripRequestId)
        );
    }

    // CREATE BID
    // Only the logged-in agency can submit a bid.
    @PostMapping
    public ResponseEntity<?> createBid(
            @RequestBody Bid bid,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not found");
        }

        if (!"AGENCY".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only agencies can submit bids");
        }

        Agency agency = agencyRepository
                .findByUserId(user.getUserId())
                .orElse(null);

        if (agency == null) {
            return ResponseEntity.status(404)
                    .body("Agency profile not found");
        }

        if (bid.getTripRequestId() == null) {
            return ResponseEntity.badRequest()
                    .body("Trip request ID is required");
        }

        if (bid.getAmount() == null ||
                bid.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest()
                    .body("A valid bid amount is required");
        }

        TripRequest tripRequest = tripRequestRepository
                .findById(bid.getTripRequestId())
                .orElse(null);

        if (tripRequest == null) {
            return ResponseEntity.status(404)
                    .body("Trip request not found");
        }

        if (!"OPEN".equalsIgnoreCase(tripRequest.getStatus())) {
            return ResponseEntity.badRequest()
                    .body("This trip is no longer accepting bids");
        }

        // Never trust the agency ID from the frontend.
        bid.setAgencyId(agency.getAgencyId());

        bid.setCreatedAt(LocalDateTime.now());
        bid.setStatus("PENDING");

        Bid savedBid = bidRepository.save(bid);

        return ResponseEntity.ok(savedBid);
    }

    // ACCEPT BID
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateBid(
            @PathVariable Long id,
            @RequestBody Bid updatedBid,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not found");
        }

        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only customers can accept bids");
        }

        if (updatedBid.getStatus() == null ||
                !"ACCEPTED".equalsIgnoreCase(updatedBid.getStatus())) {

            return ResponseEntity.badRequest()
                    .body("This endpoint only supports bid acceptance");
        }

        Bid bid = bidRepository.findById(id).orElse(null);

        if (bid == null) {
            return ResponseEntity.notFound().build();
        }

        TripRequest tripRequest = tripRequestRepository
                .findById(bid.getTripRequestId())
                .orElse(null);

        if (tripRequest == null) {
            return ResponseEntity.notFound().build();
        }

        if (tripRequest.getCustomerId() == null ||
                !tripRequest.getCustomerId().equals(user.getUserId())) {

            return ResponseEntity.status(403)
                    .body("You are not allowed to accept bids for this trip");
        }

        if (!"OPEN".equalsIgnoreCase(tripRequest.getStatus())) {
            return ResponseEntity.badRequest()
                    .body("This trip is no longer accepting bids");
        }

        if (!"PENDING".equalsIgnoreCase(bid.getStatus())) {
            return ResponseEntity.badRequest()
                    .body("Only pending bids can be accepted");
        }

        // Accept the selected bid.
        bid.setStatus("ACCEPTED");
        Bid acceptedBid = bidRepository.save(bid);

        // Reject all other pending bids for this trip.
        List<Bid> tripBids =
                bidRepository.findByTripRequestId(bid.getTripRequestId());

        for (Bid otherBid : tripBids) {

            if (!otherBid.getBidId().equals(bid.getBidId())
                    && "PENDING".equalsIgnoreCase(otherBid.getStatus())) {

                otherBid.setStatus("REJECTED");
                bidRepository.save(otherBid);
            }
        }

        // Mark the trip as booked.
        tripRequest.setStatus("BOOKED");
        tripRequestRepository.save(tripRequest);

        // Create booking automatically.
        Booking booking = new Booking();

        booking.setCustomerId(tripRequest.getCustomerId());
        booking.setAgencyId(bid.getAgencyId());
        booking.setBusId(bid.getBusId());
        booking.setTripRequestId(bid.getTripRequestId());
        booking.setBidId(bid.getBidId());
        booking.setAmount(bid.getAmount());

        booking.setBookingType("REVERSE_BID");
        booking.setStatus("CONFIRMED");

        booking.setStartDate(tripRequest.getStartDate());
        booking.setEndDate(tripRequest.getEndDate());
        booking.setCreatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        return ResponseEntity.ok(acceptedBid);
    }

    // REJECT BID
    @PutMapping("/{id}/reject")
    @Transactional
    public ResponseEntity<?> rejectBid(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not found");
        }

        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only customers can reject bids");
        }

        Bid bid = bidRepository.findById(id).orElse(null);

        if (bid == null) {
            return ResponseEntity.notFound().build();
        }

        TripRequest tripRequest = tripRequestRepository
                .findById(bid.getTripRequestId())
                .orElse(null);

        if (tripRequest == null) {
            return ResponseEntity.notFound().build();
        }

        if (tripRequest.getCustomerId() == null ||
                !tripRequest.getCustomerId().equals(user.getUserId())) {

            return ResponseEntity.status(403)
                    .body("You are not allowed to reject this bid");
        }

        if (!"OPEN".equalsIgnoreCase(tripRequest.getStatus())) {
            return ResponseEntity.badRequest()
                    .body("This trip is no longer accepting bid decisions");
        }

        if (!"PENDING".equalsIgnoreCase(bid.getStatus())) {
            return ResponseEntity.badRequest()
                    .body("Only pending bids can be rejected");
        }

        bid.setStatus("REJECTED");

        Bid rejectedBid = bidRepository.save(bid);

        return ResponseEntity.ok(rejectedBid);
    }

    // DELETE BID
    // Only the agency that submitted a pending bid can delete it.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBid(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not found");
        }

        if (!"AGENCY".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only agencies can delete their own bids");
        }

        Agency agency = agencyRepository
                .findByUserId(user.getUserId())
                .orElse(null);

        if (agency == null) {
            return ResponseEntity.status(404)
                    .body("Agency profile not found");
        }

        Bid bid = bidRepository.findById(id).orElse(null);

        if (bid == null) {
            return ResponseEntity.notFound().build();
        }

        if (bid.getAgencyId() == null ||
                !bid.getAgencyId().equals(agency.getAgencyId())) {

            return ResponseEntity.status(403)
                    .body("You cannot delete another agency's bid");
        }

        if (!"PENDING".equalsIgnoreCase(bid.getStatus())) {
            return ResponseEntity.badRequest()
                    .body("Only pending bids can be deleted");
        }

        bidRepository.delete(bid);

        return ResponseEntity.ok("Bid deleted successfully");
    }
}
