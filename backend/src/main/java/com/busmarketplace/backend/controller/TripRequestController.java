package com.busmarketplace.backend.controller;

import com.busmarketplace.backend.entity.TripRequest;
import com.busmarketplace.backend.repository.TripRequestRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trip-requests")
public class TripRequestController {

    private final TripRequestRepository tripRequestRepository;

    public TripRequestController(TripRequestRepository tripRequestRepository) {
        this.tripRequestRepository = tripRequestRepository;
    }

    @GetMapping
    public List<TripRequest> getAllTripRequests() {
        return tripRequestRepository.findAll();
    }

    @GetMapping("/{id}")
    public TripRequest getTripRequestById(@PathVariable Long id) {
        return tripRequestRepository.findById(id).orElse(null);
    }

    @PostMapping
    public TripRequest createTripRequest(@RequestBody TripRequest tripRequest) {
        return tripRequestRepository.save(tripRequest);
    }

    @PutMapping("/{id}")
    public TripRequest updateTripRequest(
            @PathVariable Long id,
            @RequestBody TripRequest updatedTripRequest) {

        TripRequest existingTripRequest =
                tripRequestRepository.findById(id).orElse(null);

        if (existingTripRequest == null) {
            return null;
        }

        existingTripRequest.setCustomerId(updatedTripRequest.getCustomerId());
        existingTripRequest.setPickupLocation(updatedTripRequest.getPickupLocation());
        existingTripRequest.setDestination(updatedTripRequest.getDestination());
        existingTripRequest.setStartDate(updatedTripRequest.getStartDate());
        existingTripRequest.setEndDate(updatedTripRequest.getEndDate());
        existingTripRequest.setPassengers(updatedTripRequest.getPassengers());
        existingTripRequest.setRequirements(updatedTripRequest.getRequirements());
        existingTripRequest.setStatus(updatedTripRequest.getStatus());

        return tripRequestRepository.save(existingTripRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteTripRequest(@PathVariable Long id) {
        tripRequestRepository.deleteById(id);
    }
}