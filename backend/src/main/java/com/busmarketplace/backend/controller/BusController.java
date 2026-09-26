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

        /*
         * Only agencies can access their agency buses.
         */
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

    @GetMapping("/{id}")
    public ResponseEntity<Bus> getBusById(@PathVariable Long id) {
        return busRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Bus createBus(@RequestBody Bus bus) {
        return busRepository.save(bus);
    }

    @PutMapping("/{id}")
    public Bus updateBus(
            @PathVariable Long id,
            @RequestBody Bus updatedBus) {

        Bus existingBus = busRepository.findById(id)
                .orElse(null);

        if (existingBus == null) {
            return null;
        }

        existingBus.setAgencyId(updatedBus.getAgencyId());
        existingBus.setBusName(updatedBus.getBusName());
        existingBus.setRegistrationNumber(
                updatedBus.getRegistrationNumber()
        );
        existingBus.setCapacity(updatedBus.getCapacity());
        existingBus.setBusType(updatedBus.getBusType());
        existingBus.setIsAc(updatedBus.getIsAc());
        existingBus.setPricePerTrip(
                updatedBus.getPricePerTrip()
        );
        existingBus.setDescription(
                updatedBus.getDescription()
        );

        return busRepository.save(existingBus);
    }

    @DeleteMapping("/{id}")
    public void deleteBus(@PathVariable Long id) {
        busRepository.deleteById(id);
    }
}