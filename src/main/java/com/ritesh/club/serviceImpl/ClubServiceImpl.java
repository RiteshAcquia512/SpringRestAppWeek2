package com.ritesh.club.serviceImpl;

import com.ritesh.club.dto.ClubDto;
import com.ritesh.club.entity.Club;
import com.ritesh.club.exception.DuplicateEmailException;
import com.ritesh.club.exception.ResourceNotFoundException;
import com.ritesh.club.mapper.ClubMapper;
import com.ritesh.club.repository.ClubRepository;
import com.ritesh.club.service.ClubService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.server.ExportException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClubServiceImpl implements ClubService {

    @Autowired
    private ClubRepository clubRepository;

    private static final Logger logger = LogManager.getLogger(ClubService.class);

    @Override
    public ClubDto createClub(ClubDto clubDto) {
        try {
            if (clubRepository.existsByEmail(clubDto.getEmail())) {
                throw new DuplicateEmailException("Failed to create club : Email already exists");
            }
            Club club = ClubMapper.mapToClub(clubDto);
            Club savedClub = clubRepository.save(club);
            ClubDto res = ClubMapper.mapToClubDto(savedClub);
            logger.info("Club Created Successfully");
            return res;
        } catch (Exception e) {
            logger.error("Error creating club: {}", e.getMessage(), e);
            if (e instanceof DuplicateEmailException) {
                throw (DuplicateEmailException) e; // Rethrow DuplicateEmailException
            } else {
                throw new RuntimeException("Failed to create club", e); // Rethrow other exceptions
            }
        }
    }



    @Override
    public ClubDto addEvent(Long id, String event) {
        try {
            Optional<Club> optionalClub = clubRepository.findById(id);
            if (optionalClub.isPresent()) {
                Club club = optionalClub.get();
                if (club.getEvents() == null || club.getEvents().isEmpty()) {
                    club.setEvents(new ArrayList<>());
                }
                club.getEvents().add(event.toLowerCase());
                Club savedClub = clubRepository.save(club);
                ClubDto res = ClubMapper.mapToClubDto(savedClub);
                return res;
            } else {
                logger.error("Club not found with id: {}", id);
                throw new ResourceNotFoundException("Club not found with id: " + id);
            }
        } catch (Exception e) {
            logger.error("Error adding event to club with id {}: {}", id, e.getMessage(), e);
            if (e instanceof ResourceNotFoundException) {
                throw (ResourceNotFoundException) e; // Rethrow DuplicateEmailException
            } else {
                throw new RuntimeException("Error adding event to club with id: "+ id); // Rethrow other exceptions
            }
        }
    }





    @Override
    public List<ClubDto> getAllClub() {
        try {
            List<Club> clubs = clubRepository.findAll();
            return clubs.stream()
                    .map(ClubMapper::mapToClubDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving all clubs: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve clubs", e);
        }
    }

    @Override
    public ClubDto getClubById(Long id) {
        logger.info("Fetching club with id: {}", id);
        try {
            Optional<Club> optionalClub = clubRepository.findById(id);
            if (optionalClub.isPresent()) {
                Club club = optionalClub.get();
                logger.debug("Club found: {}", club);
                ClubDto res = ClubMapper.mapToClubDto(club);
                return res;
            } else {
                logger.error("Club not found with id: {}", id);
                throw new ResourceNotFoundException("Club not found with id: " + id);
            }
        } catch (Exception e) {
            logger.error("Error fetching club with id {}: {}", id, e.getMessage(), e);
            if (e instanceof ResourceNotFoundException) {
                throw (ResourceNotFoundException) e; // Rethrow DuplicateEmailException
            } else {
                throw new RuntimeException("Error fetching club with id: " + id); // Rethrow other exceptions
            }
        }
    }


    @Override
    public String deleteClubById(Long id) {
        logger.info("Attempting to delete club with id: {}", id);
        try {
            if (clubRepository.existsById(id)) {
                clubRepository.deleteById(id);
                logger.info("Successfully deleted club with id: {}", id);
                return "Deleted Successfully";
            } else {
                logger.error("Club not found with id: {}", id);
                throw new ResourceNotFoundException("Club not found with id: " + id);
            }
        } catch (Exception e) {
            logger.error("Error deleting club with id {}: {}", id, e.getMessage(), e);
            if (e instanceof ResourceNotFoundException) {
                throw (ResourceNotFoundException) e; // Rethrow DuplicateEmailException
            } else {
                throw new RuntimeException("Error deleting club with id", e); // Rethrow other exceptions
            }
        }
    }

    @Override
    public String deleteEventsByName(Long id, String eventsStr) {
        logger.info("Attempting to delete events '{}' from club with id: {}", eventsStr, id);
        try {
            Optional<Club> optionalClub = clubRepository.findById(id);
            if (optionalClub.isPresent()) {
                Club club = optionalClub.get();
                List<String> events = club.getEvents();
                if (events == null || events.isEmpty()) {
                    logger.error("No events found for club with id: {}", id);
                    throw new ResourceNotFoundException("No events found for this club.");
                }

                String[] eventsArray = eventsStr.split(",");
                List<String> eventsToDelete = Arrays.asList(eventsArray);

                List<String> deletedEvents = new ArrayList<>();
                List<String> notFoundEvents = new ArrayList<>();

                for (String event : eventsToDelete) {
                    if (events.remove(event.trim().toLowerCase())) {
                        deletedEvents.add(event.trim());
                    } else {
                        notFoundEvents.add(event.trim());
                    }
                }

                clubRepository.save(club);

                StringBuilder responseMessage = new StringBuilder();
                if (!deletedEvents.isEmpty()) {
                    responseMessage.append("Deleted events: ").append(String.join(", ", deletedEvents)).append(". ");
                }
                if (!notFoundEvents.isEmpty()) {
                    responseMessage.append("Events not found: ").append(String.join(", ", notFoundEvents)).append(".");
                }

                logger.info("Successfully deleted events '{}' from club with id: {}", deletedEvents, id);
                return responseMessage.toString();
            } else {
                logger.error("Club not found with id: {}", id);
                throw new ResourceNotFoundException("Club not found with id: " + id);
            }
        } catch (Exception e) {
            logger.error("Error deleting events '{}' from club with id {}: {}", eventsStr, id, e.getMessage(), e);
            if (e instanceof ResourceNotFoundException) {
                throw (ResourceNotFoundException) e; // Rethrow DuplicateEmailException
            } else {
                throw new RuntimeException("Failed to delete events from club with id: " + id, e); // Rethrow other exceptions
            }
        }
    }


    @Override
    public ClubDto updateEvent(Long id, Boolean addEvent, ClubDto clubDto) {
        Optional<Club> optionalClub = clubRepository.findById(id);
        if (optionalClub.isPresent()) {
            Club existingClub = optionalClub.get();

            // Check if events is null or empty, and set it to an empty list if necessary
            if (clubDto.getEvents() == null || clubDto.getEvents().isEmpty()) {
                clubDto.setEvents(new ArrayList<>());
            }

            List<String> eventsToLowerCase = clubDto.getEvents().stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            if (addEvent) {
                existingClub.getEvents().addAll(eventsToLowerCase);
            } else {
                existingClub.setEvents(eventsToLowerCase);
            }

            if (clubDto.getClubname() != null && !clubDto.getClubname().isEmpty()) {
                existingClub.setClubname(clubDto.getClubname());
            }
            if (clubDto.getEmail() != null && !clubDto.getEmail().isEmpty()) {
                existingClub.setEmail(clubDto.getEmail());
            }
            if (clubDto.getAdmin() != null && !clubDto.getAdmin().isEmpty()) {
                existingClub.setAdmin(clubDto.getAdmin());
            }

            Club updatedClub = clubRepository.save(existingClub);
            return ClubMapper.mapToClubDto(updatedClub);
        } else {
            throw new ResourceNotFoundException("Club not found with id: " + id);
        }
    }



}
