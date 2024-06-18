package com.ritesh.club.controller;


import com.ritesh.club.dto.ClubDto;
import com.ritesh.club.entity.Club;
import com.ritesh.club.exception.NotEmptyException;
import com.ritesh.club.service.ClubService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/club")
public class ClubController {

    private ClubService clubService;

    @PostMapping
        public ResponseEntity<ClubDto> createClub(@RequestBody ClubDto clubDto){
            System.out.println(clubDto.getClubname());
            validateClubFields(clubDto);
            ClubDto res = clubService.createClub(clubDto);
            return new ResponseEntity<ClubDto>(res, HttpStatus.CREATED);
        }

    @PutMapping("/{id}")
    public ResponseEntity<ClubDto> addEvent(@PathVariable Long id, @RequestBody Map<String, String> event){
        ClubDto res = clubService.addEvent(id,event.get("event"));
        return new ResponseEntity<ClubDto>(res, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{id}/events/{addEvent}")
    public ResponseEntity<ClubDto> updateEvents(@PathVariable Long id, @PathVariable Boolean addEvent, @RequestBody ClubDto clubDto) {
        ClubDto res = clubService.updateEvent(id, addEvent, clubDto);
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    @GetMapping
    public ResponseEntity<List<ClubDto>> getAllClubs(){
        List<ClubDto> res = clubService.getAllClub();
        return new ResponseEntity<List<ClubDto>> (res,HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubDto> getClubById(@PathVariable Long id){
        ClubDto res = clubService.getClubById(id);
        return new ResponseEntity<ClubDto>(res,HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClubById(@PathVariable Long id){
        String res = clubService.deleteClubById(id);
        return new ResponseEntity<String>(res,HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/event")
    public ResponseEntity<String> deleteEventByName(@RequestParam Long id, @RequestParam String event){
        String response = clubService.deleteEventsByName(id, event);
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    private void validateClubFields(ClubDto clubDto) {
        List<String> errors = new ArrayList<>();
        if (clubDto.getClubname() == null || clubDto.getClubname().isEmpty()) {
            errors.add("Club name must not be empty");
        }
        if (clubDto.getEmail() == null || clubDto.getEmail().isEmpty()) {
            errors.add("Email must not be empty");
        }
        if (clubDto.getAdmin() == null || clubDto.getAdmin().isEmpty()) {
            errors.add("Admin name must not be empty");
        }
        for (String event : clubDto.getEvents()) {
            if (event == null || event.isEmpty()) {
                errors.add("Event name must not be empty");
            }
        }
        if (!errors.isEmpty()) {
            throw new NotEmptyException(String.join(", ", errors));
        }
    }

}
