package com.example.MovieBookingApp.Controller;

import com.example.MovieBookingApp.DTO.BookingDTO;
import com.example.MovieBookingApp.Entity.Booking;
import com.example.MovieBookingApp.Enums.BookingStatus;
import com.example.MovieBookingApp.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/createBooking")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingDTO bookingDTO){
        return ResponseEntity.ok(bookingService.createBooking(bookingDTO));
    }

    @GetMapping("/getUserBookings/{id}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable Long id){
        return ResponseEntity.ok(bookingService.getUserBooking(id));
    }

    @GetMapping("/getShowBookings/{id}")
    public ResponseEntity<List<Booking>> getShowBookings(@PathVariable Long id){
        return ResponseEntity.ok(bookingService.getShowBooking(id));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable Long id){
        return ResponseEntity.ok(bookingService.confirmBooking(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id){
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @GetMapping("/getBookingByStatus/{bookingStatus}")
    public ResponseEntity<List<Booking>> getBookingByStatus(@PathVariable BookingStatus bookingStatus){
        return ResponseEntity.ok(bookingService.getBookingByStatus(bookingStatus));
    }

}
