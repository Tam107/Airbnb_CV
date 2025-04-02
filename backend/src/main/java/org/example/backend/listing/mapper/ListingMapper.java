package org.example.backend.listing.mapper;

import org.example.backend.listing.application.dto.CreateListingDTO;
import org.example.backend.listing.application.dto.SaveListingDTO;
import org.example.backend.listing.domain.Listing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ListingPictureMapper.class})
public interface ListingMapper {

    /* prevents accidental overwriting of existing values in the Listing entity*/
    @Mapping(target = "landlordPublicId", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "lastModifiedDate",ignore = true)
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "createDate",ignore = true)
    @Mapping(target = "pictures",ignore = true)
    @Mapping(target = "title",source = "description.title.value")
    @Mapping(target = "description",source = "description.description.value")
    @Mapping(target = "bedrooms",source = "info.bedrooms.value")
    @Mapping(target = "guests",source = "info.guests.value")
    @Mapping(target = "bookingCategory",source = "category")
    @Mapping(target = "beds",source = "info.bed.value")
    @Mapping(target = "bathrooms",source = "info.beds.value")
    @Mapping(target = "price",source = "price.value")
    Listing saveListingDTOToListing(SaveListingDTO saveListingDTO);

    //return objects for backend
    CreateListingDTO listingToCreatedListingDTO(Listing listing);
}
