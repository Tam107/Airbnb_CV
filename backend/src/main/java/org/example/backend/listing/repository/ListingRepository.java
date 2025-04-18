package org.example.backend.listing.repository;

import org.example.backend.listing.domain.BookingCategory;
import org.example.backend.listing.domain.Listing;
import org.example.backend.listing.domain.ListingPicture;
import org.example.backend.user.application.dto.ReadUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    // Fetches all listings for a landlord with their cover pictures for display (e.g., landlord dashboard)
    @Query("SELECT listing from Listing listing left join " +
            "fetch listing.pictures picture " +
            "where listing.landlordPublicId = :landlordPublicId " +
            "and picture.isCover = true")
    List<Listing> findAllByLandlordPublicIdFetchCoverPicture(UUID landlordPublicId);

    // Deletes a specific listing if it belongs to the landlord, ensuring secure deletion
    long deleteByPublicIdAndLandlordPublicId(UUID publicId, UUID landlordPublicId);

    // Fetches paginated listings by booking category (e.g., "Entire Home") with cover pics for browsing
    @Query("select listing from Listing listing left join fetch  listing.pictures pictures " +
            "where pictures.isCover = true and listing.bookingCategory =:bookingCategory")
    Page<Listing> finaAllByBookingCategoryWithCoverOnly(Pageable pageable, BookingCategory bookingCategory);

    // Fetches all listings with cover pics only, paginated for general listing feed (e.g., homepage)
    @Query("SELECT listing from Listing listing LEFT JOIN FETCH listing.pictures picture" +
            " WHERE picture.isCover = true")
    Page<Listing> findAllWithCoverOnly(Pageable pageable);

    Optional<Listing> findByPublicId(UUID publicId);

    List<Listing> findAllByPublicIdIn(List<UUID> allListingPublicIDs);

    Optional<Listing> findOneByPublicIdAndLandlordPublicId(UUID listingPublicId, UUID landlordPublicId);

    Page<Listing> findAllByLocationAndBathroomsAndBedroomsAndGuestsAndBeds(
            Pageable pageable, String location, int bathrooms, int bedrooms, int guests,
            int beds
    );
}
