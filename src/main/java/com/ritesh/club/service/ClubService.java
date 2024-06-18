package com.ritesh.club.service;

import com.ritesh.club.dto.ClubDto;
import com.ritesh.club.entity.Club;

import java.util.List;
import java.util.Optional;

public interface ClubService {
    ClubDto createClub(ClubDto clubDto);
    ClubDto addEvent(Long id,String event);
    List<ClubDto> getAllClub();
    ClubDto getClubById(Long id);
    String deleteClubById(Long id);
    String deleteEventsByName(Long id,String event);

    ClubDto updateEvent(Long id,Boolean addEvent,ClubDto clubDto);
}
