package org.example.backend.listing.application;

import org.example.backend.listing.application.dto.DisplayCardListingDTO;
import org.example.backend.listing.domain.BookingCategory;
import org.example.backend.listing.domain.Listing;
import org.example.backend.listing.mapper.ListingMapper;
import org.example.backend.listing.repository.ListingRepository;
import org.example.backend.user.application.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// nguoi thue
@Service
public class TenantService {

    private final ListingRepository listingRepository;

    private final ListingMapper listingMapper;

    private final UserService userService;

    @Autowired
    public TenantService(ListingRepository listingRepository, ListingMapper listingMapper, UserService userService){
        this.listingRepository = listingRepository;
        this.listingMapper = listingMapper;
        this.userService = userService;
    }

    /*
    * DisplayCardListingDTO(PriceVO price,
                            String location,
                            PictureDTO cover,
                            BookingCategory bookingCategory,
                            UUID publicId)*/

    public Page<DisplayCardListingDTO> getAllByCategory(Pageable pageable, BookingCategory category){
        Page<Listing> allOrBookingCategory;
        if (category == BookingCategory.ALL){
            allOrBookingCategory = listingRepository.findAllWithCoverOnly(pageable);
        }
        allOrBookingCategory = listingRepository.finaAllByBookingCategoryWithCoverOnly(pageable,category);

        return allOrBookingCategory.map(listingMapper::listingToDisplayCardListingDTO);
    }

}
