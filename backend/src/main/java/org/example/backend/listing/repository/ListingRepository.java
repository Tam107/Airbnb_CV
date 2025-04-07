package org.example.backend.listing.repository;

import org.example.backend.listing.domain.Listing;
import org.example.backend.listing.domain.ListingPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    @Query("SELECT listing from Listing listing left join " +
            "fetch listing.pictures picture " +
            "where listing.landlordPublicId = :landlordPublicId " +
            "and picture.isCover = true")
    List<Listing> findAllByLandlordPublicIdFetchCoverPicture(UUID landlordPublicId);

}
