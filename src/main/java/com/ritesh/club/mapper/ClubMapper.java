package com.ritesh.club.mapper;

import com.ritesh.club.dto.ClubDto;
import com.ritesh.club.entity.Club;

public class ClubMapper {
    public static ClubDto mapToClubDto(Club club){
        return new ClubDto(
                club.getId(),
                club.getClubname(),
                club.getEmail(),
                club.getAdmin(),
                club.getEvents()
        );
    }

    public static Club mapToClub(ClubDto clubDto){
        return new Club(
                clubDto.getId(),
                clubDto.getClubname(),
                clubDto.getEmail(),
                clubDto.getAdmin(),
                clubDto.getEvents()
        );
    }
}
