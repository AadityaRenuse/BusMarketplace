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

        TripRequest tripRequest = tripRequestRepository
                .findById(tripRequestId)
                .orElse(null);

        if (tripRequest == null) {
            return ResponseEntity.notFound().build();
        }

        if (!tripRequest.getCustomerId().equals(user.getUserId())) {
            return ResponseEntity.status(403)
                    .body("You are not allowed to view bids for this trip");
        }

        return ResponseEntity.ok(
                bidRepository.findByTripRequestId(tripRequestId)
        );
    }

    /*
     * CREATE BID
     *
     * The agency ID is now determined from the
     * currently logged-in agency account.
     */
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

        /*
         * Only agency users can submit bids.
         */
        if (!"AGENCY".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only agencies can submit bids");
        }

        /*
         * Find the agency belonging to the logged-in user.
         */
        Agency agency = agencyRepository
                .findByUserId(user.getUserId())
                .orElse(null);

        if (agency == null) {
            return ResponseEntity.status(404)
                    .body("Agency profile not found");
        }

        /*
         * Make sure the trip exists.
         */
        TripRequest tripRequest = tripRequestRepository
                .findById(bid.getTripRequestId())
                .orElse(null);

        if (tripRequest == null) {
            return ResponseEntity.status(404)
                    .body("Trip request not found");
        }

        /*
         * Only OPEN trips can receive bids.
         */
        if (!"OPEN".equalsIgnoreCase(tripRequest.getStatus())) {
            return ResponseEntity.badRequest()
                    .body("This trip is no longer accepting bids");
        }

        /*
         * IMPORTANT:
         * Never trust agencyId sent by the frontend.
         * Use the agency connected to the logged-in user.
         */
        bid.setAgencyId(agency.getAgencyId());

        bid.setCreatedAt(LocalDateTime.now());

        if (bid.getStatus() == null || bid.getStatus().isBlank()) {
            bid.setStatus("PENDING");
        }

        Bid savedBid = bidRepository.save(bid);

        return ResponseEntity.ok(savedBid);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBid(
            @PathVariable Long id,
            @RequestBody Bid updatedBid,
            Authentication authentication) {

        Bid bid = bidRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        if ("ACCEPTED".equalsIgnoreCase(updatedBid.getStatus())) {

            String email = authentication.getName();

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                return ResponseEntity.status(401)
                        .body("User not found");
            }

            TripRequest tripRequest = tripRequestRepository
                    .findById(bid.getTripRequestId())
                    .orElseThrow(() ->
                            new RuntimeException("Trip request not found"));

            if (!tripRequest.getCustomerId().equals(user.getUserId())) {
                return ResponseEntity.status(403)
                        .body("You are not allowed to accept bids for this trip");
            }

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
                    return ResponseEntity.badRequest()
                            .body("A bid has already been accepted for this trip");
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

        bid.setTripRequestId(updatedBid.getTripRequestId());
        bid.setAgencyId(updatedBid.getAgencyId());
        bid.setBusId(updatedBid.getBusId());
        bid.setAmount(updatedBid.getAmount());
        bid.setMessage(updatedBid.getMessage());
        bid.setStatus(updatedBid.getStatus());

        return ResponseEntity.ok(bidRepository.save(bid));
    }

    @DeleteMapping("/{id}")
    public void deleteBid(@PathVariable Long id) {
        bidRepository.deleteById(id);
    }
}