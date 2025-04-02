package org.example.backend.listing.mapper;

import org.example.backend.listing.application.dto.sub.PictureDTO;
import org.example.backend.listing.domain.ListingPicture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ListingPictureMapper {

    @Mapping(target = "listingId", ignore = true)
    @Mapping(target = "listing", ignore = true)
    Set<ListingPicture> pictureDTOToListingPicture(List<PictureDTO> pictureDTOS);

    List<PictureDTO> listingPictureToPictureDTO(List<ListingPicture> listingPictures);
}
