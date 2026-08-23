package com.busmarketplace.backend.controller;

import com.busmarketplace.backend.entity.Booking;
import com.busmarketplace.backend.repository.BookingRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;

    public BookingController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {

        if (booking.getStatus() == null || booking.getStatus().isBlank()) {
            booking.setStatus("PENDING");
        }

        if (booking.getBookingType() == null || booking.getBookingType().isBlank()) {
            booking.setBookingType("BUS");
        }

        booking.setCreatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    @PutMapping("/{id}")
    public Booking updateBooking(
            @PathVariable Long id,
            @RequestBody Booking updatedBooking) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setCustomerId(updatedBooking.getCustomerId());
        booking.setAgencyId(updatedBooking.getAgencyId());
        booking.setBusId(updatedBooking.getBusId());
        booking.setTripRequestId(updatedBooking.getTripRequestId());
        booking.setBidId(updatedBooking.getBidId());
        booking.setAmount(updatedBooking.getAmount());
        booking.setBookingType(updatedBooking.getBookingType());
        booking.setStatus(updatedBooking.getStatus());
        booking.setStartDate(updatedBooking.getStartDate());
        booking.setEndDate(updatedBooking.getEndDate());

        return bookingRepository.save(booking);
    }

    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable Long id) {
        bookingRepository.deleteById(id);
    }
}