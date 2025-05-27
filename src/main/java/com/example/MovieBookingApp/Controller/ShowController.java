package com.example.MovieBookingApp.Controller;

import com.example.MovieBookingApp.DTO.ShowDTO;
import com.example.MovieBookingApp.Entity.Show;
import com.example.MovieBookingApp.Service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/show")
public class ShowController {
    @Autowired
    private ShowService showService;

    @PutMapping("/createShow")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Show> createShow(@RequestBody ShowDTO showDTO){
        return ResponseEntity.ok(showService.createShow(showDTO));
    }

    @GetMapping("/getAllShows")
    public ResponseEntity<List<Show>> getAllShows(){
        return ResponseEntity.ok(showService.getAllShows());
    }

//    list of shows by movie
    @GetMapping("/getShowByMovie/{id}")
    public ResponseEntity<List<Show>> getShowByMovie(@PathVariable Long id){
        return ResponseEntity.ok(showService.getShowByMovie(id));
    }

//    list of shows by theater
    @GetMapping("/getShowByTheater/{id}")
    public ResponseEntity<List<Show>> getShowByTheater(@PathVariable Long id){
        return ResponseEntity.ok(showService.getShowByTheater(id));
    }

//    update show
    @PutMapping("/updateShow/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Show> updateShow(@PathVariable Long id, @RequestBody ShowDTO showDTO){
        return ResponseEntity.ok(showService.updateShow(id, showDTO));
    }

//    delete show
    @DeleteMapping("/deleteShow/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShow(@PathVariable Long id){
        showService.deleteShow(id);
        return ResponseEntity.ok().build();
    }

}
