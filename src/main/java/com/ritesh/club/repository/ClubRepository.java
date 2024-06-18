package com.ritesh.club.repository;

import com.ritesh.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club,Long> {
    boolean existsByEmail(String email);
}
