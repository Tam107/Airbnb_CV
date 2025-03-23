package org.example.backend.listing.repository;

import org.example.backend.listing.domain.Listing;
import org.example.backend.listing.domain.ListingPicture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, Long> {


}
