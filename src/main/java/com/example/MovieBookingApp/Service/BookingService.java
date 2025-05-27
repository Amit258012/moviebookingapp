package com.example.MovieBookingApp.Service;

import com.example.MovieBookingApp.DTO.BookingDTO;
import com.example.MovieBookingApp.Entity.Booking;
import com.example.MovieBookingApp.Entity.Show;
import com.example.MovieBookingApp.Entity.User;
import com.example.MovieBookingApp.Enums.BookingStatus;
import com.example.MovieBookingApp.Repository.BookingRepository;
import com.example.MovieBookingApp.Repository.ShowRepository;
import com.example.MovieBookingApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private UserRepository userRepository;

    public Booking createBooking(BookingDTO bookingDTO) {
        Show show = showRepository.findById(bookingDTO.getShowId())
                                  .orElseThrow(() -> new RuntimeException("Show not found"));

        if (!isSeatsAvailable(show.getId(), bookingDTO.getNumberOfSeats())) {
            throw new RuntimeException("Not enough seat are available");
        }

        if (bookingDTO.getSeatNumbers()
                      .size()!=bookingDTO.getNumberOfSeats()) {
            throw new RuntimeException("Selected Seat Numbers size and Number of seats must be equal");
        }

        // Validate Duplicate Seat booking
        validateDuplicateSeats(show.getId(), bookingDTO.getSeatNumbers());

        User user = userRepository.findById(bookingDTO.getUserId())
                                  .orElseThrow(() -> new RuntimeException("User not found!"));

        Booking booking = new Booking();

        booking.setUser(user);
        booking.setShow(show);
        booking.setNumberOfSeats(bookingDTO.getNumberOfSeats());
        booking.setSeatNumbers(bookingDTO.getSeatNumbers());
        booking.setPrice(calculateTotalAmount(show.getPrice(), bookingDTO.getNumberOfSeats()));
        booking.setBookingTime(LocalDateTime.now());
        booking.setBookingStatus(BookingStatus.PENDING);

        return bookingRepository.save(booking);
    }

    public boolean isSeatsAvailable(Long showId, Integer numberOfSeat) {
        Show show = showRepository.findById(showId)
                                  .orElseThrow(() -> new RuntimeException("Show not found"));

        int bookedSeats = show.getBookings()
                              .stream()
                              .filter(booking -> booking.getBookingStatus()!=BookingStatus.CANCELLED)
                              .mapToInt(Booking::getNumberOfSeats)
                              .sum();
        return (show.getTheater()
                    .getTheaterCapacity() - bookedSeats >= numberOfSeat);

    }

    public void validateDuplicateSeats(Long showId, List<String> seatNumbers) {
        Show show = showRepository.findById(showId)
                                  .orElseThrow(() -> new RuntimeException("Show not found"));

        Set<String> occupiedSeats = show.getBookings()
                                        .stream()
                                        .filter(b -> b.getBookingStatus()!=BookingStatus.CANCELLED)
                                        .flatMap(b -> b.getSeatNumbers()
                                                       .stream())
                                        .collect(Collectors.toSet());

        List<String> duplicateSeats = seatNumbers.stream()
                                                 .filter(occupiedSeats::contains)
                                                 .collect(Collectors.toList());

        if (!duplicateSeats.isEmpty()) {
            throw new RuntimeException("These seats are already booked : " + duplicateSeats.toString());
        }

    }


    public Double calculateTotalAmount(Double price, Integer numberOfSeats) {
        return price * numberOfSeats;
    }

    public List<Booking> getUserBooking(Long userId) {
        Optional<List<Booking>> bookingListBox = bookingRepository.findByUserId(userId);

        if (bookingListBox.isPresent()) {
            return bookingListBox.get();
        } else {
            throw new RuntimeException("No bookings found for userId : " + userId);
        }
    }

    public List<Booking> getShowBooking(Long showId) {
        Optional<List<Booking>> bookingListBox = bookingRepository.findByShowId(showId);

        if (bookingListBox.isPresent()) {
            return bookingListBox.get();
        } else {
            throw new RuntimeException("No bookings found for showId : " + showId);
        }
    }

    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new RuntimeException("Booking not found"));


        if (booking.getBookingStatus()!=BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not in pending state");
        }

        // payment process.....

        // After Payment process
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);

    }

    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new RuntimeException("No booking found"));

        validateCancellation(booking);
        booking.setBookingStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    public void validateCancellation(Booking booking) {
        LocalDateTime showTime = booking.getShow()
                                        .getShowTime();
        LocalDateTime deadlineTime = showTime.minusHours(2);
        if (LocalDateTime.now()
                         .isAfter(deadlineTime)) {
            throw new RuntimeException("Cannot cancel the booking after deadline");
        }
        if (booking.getBookingStatus()==BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking already been cancelled");
        }
    }

    public List<Booking> getBookingByStatus(BookingStatus bookingStatus) {
        return bookingRepository.findByBookingStatus(bookingStatus);
    }

}
