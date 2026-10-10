
package com.busmarketplace.backend.controller;

import com.busmarketplace.backend.entity.Agency;
import com.busmarketplace.backend.entity.Bus;
import com.busmarketplace.backend.entity.User;
import com.busmarketplace.backend.repository.AgencyRepository;
import com.busmarketplace.backend.repository.BusRepository;
import com.busmarketplace.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusRepository busRepository;
    private final UserRepository userRepository;
    private final AgencyRepository agencyRepository;

    public BusController(
            BusRepository busRepository,
            UserRepository userRepository,
            AgencyRepository agencyRepository) {

        this.busRepository = busRepository;
        this.userRepository = userRepository;
        this.agencyRepository = agencyRepository;
    }

    /*
     * Get all buses.
     * Used for the public marketplace.
     */
    @GetMapping
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    /*
     * Get buses belonging to the logged-in agency.
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyBuses(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not found");
        }

        if (!"AGENCY".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only agencies can view agency buses");
        }

        Agency agency = agencyRepository
                .findByUserId(user.getUserId())
                .orElse(null);

        if (agency == null) {
            return ResponseEntity.status(404)
                    .body("Agency profile not found");
        }

        return ResponseEntity.ok(
                busRepository.findByAgencyId(agency.getAgencyId())
        );
    }

    /*
     * Get a specific bus.
     * Public endpoint because buses may be displayed
     * in the marketplace.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Bus> getBusById(
            @PathVariable Long id) {

        return busRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /*
     * Create a bus for the logged-in agency.
     *
     * IMPORTANT:
     * agencyId is NOT trusted from the frontend.
     */
    @PostMapping
    public ResponseEntity<?> createBus(
            @RequestBody Bus bus,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not found");
        }

        if (!"AGENCY".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only agencies can add buses");
        }

        Agency agency = agencyRepository
                .findByUserId(user.getUserId())
                .orElse(null);

        if (agency == null) {
            return ResponseEntity.status(404)
                    .body("Agency profile not found");
        }

        bus.setAgencyId(agency.getAgencyId());

        return ResponseEntity.ok(
                busRepository.save(bus)
        );
    }

    /*
     * Update a bus.
     *
     * Only the agency that owns the bus can update it.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBus(
            @PathVariable Long id,
            @RequestBody Bus updatedBus,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not found");
        }

        if (!"AGENCY".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only agencies can update buses");
        }

        Agency agency = agencyRepository
                .findByUserId(user.getUserId())
                .orElse(null);

        if (agency == null) {
            return ResponseEntity.status(404)
                    .body("Agency profile not found");
        }

        Bus existingBus = busRepository
                .findById(id)
                .orElse(null);

        if (existingBus == null) {
            return ResponseEntity.notFound().build();
        }

        if (!existingBus.getAgencyId()
                .equals(agency.getAgencyId())) {

            return ResponseEntity.status(403)
                    .body("You can only update your own buses");
        }

        existingBus.setBusName(
                updatedBus.getBusName()
        );

        existingBus.setRegistrationNumber(
                updatedBus.getRegistrationNumber()
        );

        existingBus.setCapacity(
                updatedBus.getCapacity()
        );

        existingBus.setBusType(
                updatedBus.getBusType()
        );

        existingBus.setIsAc(
                updatedBus.getIsAc()
        );

        existingBus.setPricePerTrip(
                updatedBus.getPricePerTrip()
        );

        existingBus.setDescription(
                updatedBus.getDescription()
        );

        return ResponseEntity.ok(
                busRepository.save(existingBus)
        );
    }

    /*
     * Delete a bus.
     *
     * Only the agency that owns the bus can delete it.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBus(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not found");
        }

        if (!"AGENCY".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only agencies can delete buses");
        }

        Agency agency = agencyRepository
                .findByUserId(user.getUserId())
                .orElse(null);

        if (agency == null) {
            return ResponseEntity.status(404)
                    .body("Agency profile not found");
        }

        Bus existingBus = busRepository
                .findById(id)
                .orElse(null);

        if (existingBus == null) {
            return ResponseEntity.notFound().build();
        }

        if (!existingBus.getAgencyId()
                .equals(agency.getAgencyId())) {

            return ResponseEntity.status(403)
                    .body("You can only delete your own buses");
        }

        busRepository.delete(existingBus);

        return ResponseEntity.ok(
                "Bus deleted successfully"
        );
    }
}
