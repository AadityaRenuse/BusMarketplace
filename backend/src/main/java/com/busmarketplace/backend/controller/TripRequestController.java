
package com.busmarketplace.backend.controller;

import com.busmarketplace.backend.entity.TripRequest;
import com.busmarketplace.backend.entity.User;
import com.busmarketplace.backend.repository.TripRequestRepository;
import com.busmarketplace.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/trip-requests")
public class TripRequestController {

    private final TripRequestRepository tripRequestRepository;
    private final UserRepository userRepository;

    public TripRequestController(
            TripRequestRepository tripRequestRepository,
            UserRepository userRepository) {

        this.tripRequestRepository = tripRequestRepository;
        this.userRepository = userRepository;
    }

    // GET ALL TRIP REQUESTS
    // Agencies need this to browse available trips.
    @GetMapping
    public List<TripRequest> getAllTripRequests() {
        return tripRequestRepository.findAll();
    }

    // GET TRIP REQUESTS OF LOGGED-IN CUSTOMER
    @GetMapping("/my")
    public ResponseEntity<?> getMyTripRequests(
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only customers can view their trip requests");
        }

        return ResponseEntity.ok(
                tripRequestRepository.findByCustomerId(user.getUserId())
        );
    }

    // GET TRIP REQUEST BY ID
    @GetMapping("/{id}")
    public ResponseEntity<TripRequest> getTripRequestById(
            @PathVariable Long id) {

        return tripRequestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // CREATE TRIP REQUEST
    @PostMapping
    public ResponseEntity<?> createTripRequest(
            @RequestBody TripRequest tripRequest,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only customers can create trip requests");
        }

        // Always assign the logged-in customer's ID.
        tripRequest.setCustomerId(user.getUserId());

        TripRequest savedTrip = tripRequestRepository.save(tripRequest);

        return ResponseEntity.ok(savedTrip);
    }

    // UPDATE OWN TRIP REQUEST
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTripRequest(
            @PathVariable Long id,
            @RequestBody TripRequest updatedTripRequest,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only customers can update trip requests");
        }

        TripRequest existingTripRequest =
                tripRequestRepository.findById(id).orElse(null);

        if (existingTripRequest == null) {
            return ResponseEntity.notFound().build();
        }

        if (existingTripRequest.getCustomerId() == null ||
                !existingTripRequest.getCustomerId()
                        .equals(user.getUserId())) {

            return ResponseEntity.status(403)
                    .body("You cannot update another customer's trip");
        }

        // Update only editable trip details.
        // Customer ID and status cannot be changed by the request body.
        existingTripRequest.setPickupLocation(
                updatedTripRequest.getPickupLocation()
        );

        existingTripRequest.setDestination(
                updatedTripRequest.getDestination()
        );

        existingTripRequest.setStartDate(
                updatedTripRequest.getStartDate()
        );

        existingTripRequest.setEndDate(
                updatedTripRequest.getEndDate()
        );

        existingTripRequest.setPassengers(
                updatedTripRequest.getPassengers()
        );

        existingTripRequest.setRequirements(
                updatedTripRequest.getRequirements()
        );

        TripRequest savedTrip =
                tripRequestRepository.save(existingTripRequest);

        return ResponseEntity.ok(savedTrip);
    }

    // DELETE OWN TRIP REQUEST
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTripRequest(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only customers can delete trip requests");
        }

        TripRequest tripRequest =
                tripRequestRepository.findById(id).orElse(null);

        if (tripRequest == null) {
            return ResponseEntity.notFound().build();
        }

        if (tripRequest.getCustomerId() == null ||
                !tripRequest.getCustomerId().equals(user.getUserId())) {

            return ResponseEntity.status(403)
                    .body("You cannot delete another customer's trip");
        }

        tripRequestRepository.delete(tripRequest);

        return ResponseEntity.ok("Trip request deleted successfully");
    }
}
