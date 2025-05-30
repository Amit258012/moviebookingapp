package com.example.MovieBookingApp.Repository;

import com.example.MovieBookingApp.Entity.Booking;
import com.example.MovieBookingApp.Enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<List<Booking>> findByUserId(Long userId);
    Optional<List<Booking>> findByShowId(Long showId);
    List<Booking> findByBookingStatus(BookingStatus bookingStatus);
}
