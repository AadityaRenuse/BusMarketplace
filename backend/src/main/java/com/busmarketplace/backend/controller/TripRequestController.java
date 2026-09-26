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

    @GetMapping
    public List<TripRequest> getAllTripRequests() {
        return tripRequestRepository.findAll();
    }

    
    @GetMapping("/my")
    public ResponseEntity<?> getMyTripRequests(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                tripRequestRepository
                        .findByCustomerId(user.getUserId())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripRequest> getTripRequestById(
            @PathVariable Long id) {

        return tripRequestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(
                        () -> ResponseEntity.notFound().build()
                );
    }

    @PostMapping
    public ResponseEntity<?> createTripRequest(
            @RequestBody TripRequest tripRequest,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(401)
                    .body("User not found");
        }

        tripRequest.setCustomerId(user.getUserId());

        TripRequest savedTrip =
                tripRequestRepository.save(tripRequest);

        return ResponseEntity.ok(savedTrip);
    }

    
    @PutMapping("/{id}")
    public TripRequest updateTripRequest(
            @PathVariable Long id,
            @RequestBody TripRequest updatedTripRequest) {

        TripRequest existingTripRequest =
                tripRequestRepository.findById(id)
                        .orElse(null);

        if (existingTripRequest == null) {
            return null;
        }

        existingTripRequest.setCustomerId(
                updatedTripRequest.getCustomerId()
        );

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

        existingTripRequest.setStatus(
                updatedTripRequest.getStatus()
        );

        return tripRequestRepository.save(existingTripRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteTripRequest(@PathVariable Long id) {
        tripRequestRepository.deleteById(id);
    }
}