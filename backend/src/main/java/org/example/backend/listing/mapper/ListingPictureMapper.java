package org.example.backend.listing.mapper;

import org.example.backend.listing.application.dto.sub.PictureDTO;
import org.example.backend.listing.domain.ListingPicture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ListingPictureMapper {

    Set<ListingPicture> pictureDTOToListingPicture(List<PictureDTO> pictureDTOS);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "listing", ignore = true)  // Ensure the `listing` property is correctly ignored
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "cover", source = "isCover")  // Mapping `cover` from DTO to isCover
    ListingPicture pictureDTOToListingPicture(PictureDTO pictureDTO);

    List<PictureDTO> listingPictureToPictureDTO(List<ListingPicture> listingPictures);

    @Mapping(target = "isCover", source = "cover")  // Map `isCover` to `cover` in DTO
    PictureDTO convertToPictureDTO(ListingPicture listingPicture);

    @Named("extract-cover")
    default PictureDTO extractCover(Set<ListingPicture> pictures) {
        return pictures.stream().findFirst().map(this::convertToPictureDTO).orElseThrow();
    }
}
