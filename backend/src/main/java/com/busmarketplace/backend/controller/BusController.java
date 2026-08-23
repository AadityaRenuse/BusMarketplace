package com.busmarketplace.backend.controller;

import com.busmarketplace.backend.entity.Bus;
import com.busmarketplace.backend.repository.BusRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusRepository busRepository;

    public BusController(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    @GetMapping
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    @GetMapping("/{id}")
    public Bus getBusById(@PathVariable Long id) {
        return busRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Bus createBus(@RequestBody Bus bus) {
        return busRepository.save(bus);
    }

    @PutMapping("/{id}")
    public Bus updateBus(@PathVariable Long id, @RequestBody Bus updatedBus) {

        Bus existingBus = busRepository.findById(id).orElse(null);

        if (existingBus == null) {
            return null;
        }

        existingBus.setAgencyId(updatedBus.getAgencyId());
        existingBus.setBusName(updatedBus.getBusName());
        existingBus.setRegistrationNumber(updatedBus.getRegistrationNumber());
        existingBus.setCapacity(updatedBus.getCapacity());
        existingBus.setBusType(updatedBus.getBusType());
        existingBus.setIsAc(updatedBus.getIsAc());
        existingBus.setPricePerTrip(updatedBus.getPricePerTrip());
        existingBus.setDescription(updatedBus.getDescription());

        return busRepository.save(existingBus);
    }

    @DeleteMapping("/{id}")
    public void deleteBus(@PathVariable Long id) {
        busRepository.deleteById(id);
    }
}