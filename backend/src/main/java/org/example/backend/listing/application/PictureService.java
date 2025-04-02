package org.example.backend.listing.application;

import lombok.RequiredArgsConstructor;
import org.example.backend.listing.application.dto.sub.PictureDTO;
import org.example.backend.listing.domain.Listing;
import org.example.backend.listing.domain.ListingPicture;
import org.example.backend.listing.mapper.ListingPictureMapper;
import org.example.backend.listing.repository.ListingPictureRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PictureService {

    private final ListingPictureRepository listingPictureRepository;

    private ListingPictureMapper listingPictureMapper;


    /**
     * Saves a list of pictures for a specific listing
     *
     * @param pictures List of PictureDTO objects to be saved
     * @param listing The Listing these pictures belong to
     * @return List of saved PictureDTO objects
     */
    public List<PictureDTO> saveAll(List<PictureDTO> pictures, Listing listing) {
        // Convert DTO objects to entity objects
        Set<ListingPicture> listingPictures = listingPictureMapper.pictureDTOToListingPicture(pictures);

        // Set the first picture as the cover image
        boolean isFirst = true;
        for (ListingPicture listingPicture : listingPictures) {
            // Mark the first image as cover (isCover = true)
            listingPicture.setCover(isFirst);
            // Associate each picture with the listing
            listingPicture.setListing(listing);
            // After processing the first image, set flag to false for remaining images
            isFirst = false;
        }

        // Save all pictures to the database
        listingPictureRepository.saveAll(listingPictures);

        // Convert back to DTOs and return
        return listingPictureMapper.listingPictureToPictureDTO(listingPictures.stream().toList());
    }
}
