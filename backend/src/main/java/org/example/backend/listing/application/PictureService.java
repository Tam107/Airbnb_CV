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


    public List<PictureDTO> saveAll(List<PictureDTO> pictures, Listing listing){
        // covert to entity
        Set<ListingPicture> listingPictures = listingPictureMapper.pictureDTOToListingPicture(pictures);

        // Set the first picture as the cover image
        boolean isFirst = true;
        for (ListingPicture listingPicture : listingPictures){
            // mark image as the cover image
            listingPicture.setCover(isFirst);
            // Associate each picture with the listing
            listingPicture.setListing(listing);

            isFirst =false;
        }

        listingPictureRepository.saveAll(listingPictures);
        return listingPictureMapper.listingPictureToPictureDTO(listingPictures.stream().toList());

    }

}
