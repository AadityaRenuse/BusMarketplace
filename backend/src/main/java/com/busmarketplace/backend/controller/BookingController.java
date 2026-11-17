
package com.busmarketplace.backend.controller;

import com.busmarketplace.backend.entity.Booking;
import com.busmarketplace.backend.entity.User;
import com.busmarketplace.backend.repository.BookingRepository;
import com.busmarketplace.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public BookingController(
            BookingRepository bookingRepository,
            UserRepository userRepository) {

        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    // GET BOOKINGS OF LOGGED-IN CUSTOMER
    @GetMapping("/my")
    public ResponseEntity<?> getMyBookings(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not found");
        }

        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403)
                    .body("Only customers can view customer bookings");
        }

        List<Booking> customerBookings = bookingRepository.findAll()
                .stream()
                .filter(booking ->
                        booking.getCustomerId() != null
                        && booking.getCustomerId().equals(user.getUserId()))
                .toList();

        return ResponseEntity.ok(customerBookings);
    }

    // GET A SINGLE BOOKING
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not found");
        }

        Booking booking = bookingRepository.findById(id).orElse(null);

        if (booking == null) {
            return ResponseEntity.notFound().build();
        }

if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
    return ResponseEntity.status(403)
            .body("Only customers can view customer bookings");
}

    if (booking.getCustomerId() == null ||
            !booking.getCustomerId().equals(user.getUserId())) {
        return ResponseEntity.status(403)
                .body("You are not allowed to view this booking");
    }

        return ResponseEntity.ok(booking);
    }
}
