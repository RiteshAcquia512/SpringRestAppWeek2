package com.ritesh.club.dto;


import com.ritesh.club.entity.Club;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubDto {

    private Long id;
    private String clubname;
    private String email;
    private String admin;
    private List<String> events = new ArrayList<>();
}
