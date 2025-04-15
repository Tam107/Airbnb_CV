package org.example.backend.listing.application;

import org.example.backend.booking.application.BookingService;
import org.example.backend.listing.application.dto.DisplayCardListingDTO;
import org.example.backend.listing.application.dto.DisplayListingDTO;
import org.example.backend.listing.application.dto.SearchDTO;
import org.example.backend.listing.application.dto.sub.LandLordListingDTO;
import org.example.backend.listing.domain.BookingCategory;
import org.example.backend.listing.domain.Listing;
import org.example.backend.listing.mapper.ListingMapper;
import org.example.backend.listing.repository.ListingRepository;
import org.example.backend.sharekernel.service.State;
import org.example.backend.user.application.UserService;
import org.example.backend.user.application.dto.ReadUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// nguoi thue
@Service
public class TenantService {

    private final ListingRepository listingRepository;

    private final ListingMapper listingMapper;

    private final UserService userService;
    private final BookingService bookingService;

    @Autowired
    public TenantService(ListingRepository listingRepository, ListingMapper listingMapper, UserService userService, BookingService bookingService){
        this.listingRepository = listingRepository;
        this.listingMapper = listingMapper;
        this.userService = userService;
        this.bookingService = bookingService;
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


    @Transactional(readOnly = true)
    public State<DisplayListingDTO, String> getOne(UUID publicId){
        Optional<Listing> listingPublicIdOpt =  listingRepository.findByPublicId(publicId);

        if(listingPublicIdOpt.isEmpty()){
            return State.<DisplayListingDTO, String> builder()
                    .forError(String.format("Listing with public id %s not found", publicId));
        }

        DisplayListingDTO displayListingDTO = listingMapper.listingToDisplayListingDTO(listingPublicIdOpt.get());

        ReadUserDTO readUserDTO = userService.getByPublicId(listingPublicIdOpt.get().getLandlordPublicId()).orElseThrow();
        LandLordListingDTO landLordListingDTO = new LandLordListingDTO(readUserDTO.firstName(), readUserDTO.imageUrl());
        displayListingDTO.setLandlord(landLordListingDTO);

        return State.<DisplayListingDTO, String>builder().forSuccess(displayListingDTO);
    }

    @Transactional(readOnly = true)
    public Page<DisplayCardListingDTO> search(Pageable pageable, SearchDTO searchDTO){
        Page<Listing> allMatchedListings = listingRepository.findAllByLocationAndBathroomsAndBedroomsAndGuestsAndBeds(
                pageable, searchDTO.location(),
                searchDTO.infos().baths().value(),
                searchDTO.infos().beds().value(),
                searchDTO.infos().bedrooms().value(),
                searchDTO.infos().guests().value()
        );
        List<UUID> listingUUIDs =  allMatchedListings.stream().map(Listing::getPublicId).toList();

        List<UUID> bookingUUIDs = bookingService.getBookingMatchByListingIdsAndBookedDate(listingUUIDs, searchDTO.dates());
        List<DisplayCardListingDTO> listingNotBooked = allMatchedListings.stream()
                .filter(listing -> !bookingUUIDs.contains(listing.getPublicId()))
                .map(listingMapper::listingToDisplayCardListingDTO)
                .toList();

        return new PageImpl<>(listingNotBooked, pageable, listingNotBooked.size());
    }

}
