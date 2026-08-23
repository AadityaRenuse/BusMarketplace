package com.busmarketplace.backend.controller;

import com.busmarketplace.backend.entity.Agency;
import com.busmarketplace.backend.repository.AgencyRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agencies")
public class AgencyController {

    private final AgencyRepository agencyRepository;

    public AgencyController(AgencyRepository agencyRepository) {
        this.agencyRepository = agencyRepository;
    }

    @GetMapping
    public List<Agency> getAllAgencies() {
        return agencyRepository.findAll();
    }

    @GetMapping("/{id}")
    public Agency getAgencyById(@PathVariable Long id) {
        return agencyRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Agency createAgency(@RequestBody Agency agency) {
        return agencyRepository.save(agency);
    }

    @PutMapping("/{id}")
    public Agency updateAgency(
            @PathVariable Long id,
            @RequestBody Agency updatedAgency) {

        Agency existingAgency =
                agencyRepository.findById(id).orElse(null);

        if (existingAgency == null) {
            return null;
        }

        existingAgency.setUserId(updatedAgency.getUserId());
        existingAgency.setAgencyName(updatedAgency.getAgencyName());
        existingAgency.setPhone(updatedAgency.getPhone());
        existingAgency.setAddress(updatedAgency.getAddress());
        existingAgency.setVerificationStatus(
                updatedAgency.getVerificationStatus()
        );

        return agencyRepository.save(existingAgency);
    }

    @DeleteMapping("/{id}")
    public void deleteAgency(@PathVariable Long id) {
        agencyRepository.deleteById(id);
    }
}