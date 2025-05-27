package com.example.MovieBookingApp.Service;

import com.example.MovieBookingApp.DTO.ShowDTO;
import com.example.MovieBookingApp.Entity.Booking;
import com.example.MovieBookingApp.Entity.Movie;
import com.example.MovieBookingApp.Entity.Show;
import com.example.MovieBookingApp.Entity.Theater;
import com.example.MovieBookingApp.Repository.MovieRepository;
import com.example.MovieBookingApp.Repository.ShowRepository;
import com.example.MovieBookingApp.Repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;

@Service
public class ShowService {
    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    public Show createShow(ShowDTO showDTO){
        Movie movie = movieRepository.findById(showDTO.getMovieId())
                                     .orElseThrow(()-> new RuntimeException("No Movie found!"));

        Theater theater= theaterRepository.findById(showDTO.getTheaterId())
                                          .orElseThrow(()-> new RuntimeException("No theater found!"));

        Show show = new Show();

        show.setShowTime(showDTO.getShowTime());
        show.setPrice(showDTO.getPrice());
        show.setMovie(movie);
        show.setTheater(theater);

        return showRepository.save(show);
    }

    public List<Show> getAllShows(){
        return showRepository.findAll();
    }

    public List<Show> getShowByMovie(Long id){
        Optional<List<Show>> showListBox = showRepository.findByMovieId(id);
        if(showListBox.isPresent()){
            return showListBox.get();
        }
        else throw new RuntimeException("No show available for movie ");
    }

    public List<Show> getShowByTheater(Long id){
        Optional<List<Show>> showListBox = showRepository.findByTheaterId(id);
        if(showListBox.isPresent()){
            return showListBox.get();
        }
        else throw new RuntimeException("No show available for Theater ");
    }

    public Show updateShow(Long id, ShowDTO showDTO){
        Show show = showRepository.findById(id)
                                  .orElseThrow(()-> new RuntimeException("No Show found with id : "+ id));

        Movie movie = movieRepository.findById(showDTO.getMovieId())
                                     .orElseThrow(()-> new RuntimeException("No Movie found!"));

        Theater theater= theaterRepository.findById(showDTO.getTheaterId())
                                          .orElseThrow(()-> new RuntimeException("No theater found!"));


        show.setShowTime(showDTO.getShowTime());
        show.setPrice(showDTO.getPrice());
        show.setMovie(movie);
        show.setTheater(theater);

        return showRepository.save(show);
    }

    public void deleteShow(Long id){
        if (!showRepository.existsById(id)){
            throw new RuntimeException("No Show available for the id "+ id);
        }
        List<Booking> bookings = showRepository.findById(id).get()
                                               .getBookings();

        if(!bookings.isEmpty()){
            throw new RuntimeException("Can't delete show with existing bookings");
        }

        showRepository.deleteById(id);
    }

}
