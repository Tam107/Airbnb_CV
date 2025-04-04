package org.example.backend.listing.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.example.backend.listing.application.LandlordService;
import org.example.backend.listing.application.dto.CreateListingDTO;
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
     * Handles creating a new listing from a multipart request
     *
     * @param request Multipart request containing images
     * @param saveListingDTOString represent of saveListingDTO
     * @return response entity
     * @throws IOException if parsing the request fails*/

    public ResponseEntity<CreateListingDTO> create(
            MultipartHttpServletRequest request,
            @RequestParam(name = "dto") String saveListingDTOString
    ) throws IOException{
        // Extract picture files from the request and map them to PictureDTO
        List<PictureDTO> pictures = request.getFileMap()
                .values()
                .stream().map(mapMultipartFileToPictureDTO())
                .toList();

//        Convert to object
        SaveListingDTO saveListingDTO = objectMapper.readValue(saveListingDTOString, SaveListingDTO.class);

        // associate extracted pictures with the listing DTO
        saveListingDTO.setPictures(pictures);

        // validate the DTO to ensure all constraint are met
        Set<ConstraintViolation<SaveListingDTO>> violations = validator.validate(saveListingDTO);
        if (!violations.isEmpty() ){
            // create a response if validation errors
            String violationJoined = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " "+ violation.getMessage())
                    .collect(Collectors.joining());
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, violationJoined);
            return ResponseEntity.of(problemDetail).build();
        }

        return ResponseEntity.ok(landlordService.create(saveListingDTO));
    }

    /**
     * Maps a multipart file to a PictureDTO object
     * @return function that transform a multipartFile into PictureDTO*/
    private Function<MultipartFile, PictureDTO> mapMultipartFileToPictureDTO() {
        return multipartFile -> {
            try {
                return new PictureDTO(multipartFile.getBytes(), multipartFile.getContentType(), false);
            }catch (IOException ioe){
                throw  new UserException("Cannot parse multipart file");
            }
        };
    }


}
