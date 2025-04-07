package org.example.backend.listing.repository;

import org.example.backend.listing.application.dto.CreatedListingDTO;
import org.example.backend.listing.application.dto.SaveListingDTO;
import org.example.backend.listing.domain.Listing;
import org.example.backend.listing.domain.ListingPicture;
import org.mapstruct.Mapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingPictureRepository extends JpaRepository<ListingPicture, Long> {



}
