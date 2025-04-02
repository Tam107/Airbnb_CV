package org.example.backend.listing.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.example.backend.listing.application.LandlordService;
import org.example.backend.listing.application.dto.CreatedListingDTO;
import org.example.backend.listing.application.dto.SaveListingDTO;
import org.example.backend.listing.application.dto.sub.PictureDTO;
import org.example.backend.user.application.UserException;
import org.example.backend.user.application.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/landlord-listing")
@RequiredArgsConstructor
public class LandlordResource {

    private final LandlordService landlordService;

    private final Validator validator;
    private final UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handles creating a new listing from a multipart request.
     *
     * @param request Multipart request containing images
     * @param saveListingDTOString JSON string representation of SaveListingDTO
     * @return ResponseEntity containing CreatedListingDTO
     * @throws IOException If parsing the request fails
     */
    public ResponseEntity<CreatedListingDTO> create(
            MultipartHttpServletRequest request,
            @RequestParam(name = "dto") String saveListingDTOString
    ) throws IOException {

        // Extract picture files from the request and map them to PictureDTO objects
        List<PictureDTO> pictures = request.getFileMap()
                .values()
                .stream().map(mapMultipartFileToPictureDTO())
                .toList();

        // Convert the JSON string to a SaveListingDTO object
        SaveListingDTO saveListingDTO = objectMapper.readValue(saveListingDTOString, SaveListingDTO.class);

        // Associate extracted pictures with the listing DTO
        saveListingDTO.setPictures(pictures);

        // Validate the DTO to ensure all constraints are met
        Set<ConstraintViolation<SaveListingDTO>> violations = validator.validate(saveListingDTO);
        if (!violations.isEmpty()) {
            // If there are validation errors, create a response with error details
            String violationsJoined = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.joining());
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, violationsJoined);
            return ResponseEntity.of(problemDetail).build();
        }

        // If validation passes, create the listing and return the response
        return ResponseEntity.ok(landlordService.create(saveListingDTO));
    }

    /**
     * Maps a MultipartFile to a PictureDTO object.
     *
     * @return Function that transforms a MultipartFile into a PictureDTO
     */
    private static Function<MultipartFile, PictureDTO> mapMultipartFileToPictureDTO() {
        return multipartFile -> {
            try {
                // Convert file content to a PictureDTO object
                return new PictureDTO(multipartFile.getBytes(), multipartFile.getContentType(), false);
            } catch (IOException ioe) {
                // Handle errors when parsing the file
                throw new UserException(String.format("Cannot parse multipart file: %s", multipartFile.getOriginalFilename()));
            }
        };
    }
}

