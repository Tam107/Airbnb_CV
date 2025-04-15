package org.example.backend.booking.application;

import org.example.backend.booking.application.dto.BookedDateDTO;
import org.example.backend.booking.application.dto.BookedListingDTO;
import org.example.backend.booking.application.dto.NewBookingDTO;
import org.example.backend.booking.domain.Booking;
import org.example.backend.booking.mapper.BookingMapper;
import org.example.backend.booking.repository.BookingRepository;
import org.example.backend.infrastructure.config.SecurityUtils;
import org.example.backend.listing.application.LandlordService;
import org.example.backend.listing.application.dto.DisplayCardListingDTO;
import org.example.backend.listing.application.dto.DisplayListingDTO;
import org.example.backend.listing.application.dto.ListingCreateBookingDTO;
import org.example.backend.listing.application.dto.vo.PriceVO;
import org.example.backend.sharekernel.service.State;
import org.example.backend.user.application.UserService;
import org.example.backend.user.application.dto.ReadUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final UserService userService;

    private final LandlordService landlordService;

    @Autowired
    public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper, UserService userService, LandlordService landlordService) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userService = userService;
        this.landlordService = landlordService;
    }

    @Transactional(rollbackFor = Exception.class)
    public State<Void, String> create(NewBookingDTO newBookingDTO) {
        Booking booking = bookingMapper.newBookingToBooking(newBookingDTO);
        Optional<ListingCreateBookingDTO> listingOpt = landlordService.getByListingPublicId(newBookingDTO.listingPublicId());

        if (listingOpt.isEmpty()) {
            return State.<Void, String>builder().forError("Landlord public id is not found");
        }
        boolean alreadyBooked = bookingRepository.bookingExistsAtInterval(newBookingDTO.startDate(), newBookingDTO.endDate(), newBookingDTO.listingPublicId());

        if (alreadyBooked) {
            return State.<Void, String>builder().forError("This listing is already booked for the selected dates");
        }

        ListingCreateBookingDTO listingCreateBookingDTO = listingOpt.get();

        booking.setFkListing(listingOpt.get().listingPublicId());

        ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSecurityContext();
        booking.setFkTenant(connectedUser.publicId());
        booking.setNumberOfTravelers(1);

        long numberOfNights = ChronoUnit.DAYS.between(newBookingDTO.startDate(), newBookingDTO.endDate());
        booking.setTotalPrice((int) (numberOfNights * listingCreateBookingDTO.price().value()));

        bookingRepository.save(booking);
        return State.<Void, String>builder().forSuccess();
    }


    @Transactional(readOnly = true)
    public List<BookedDateDTO> checkAvailability(UUID publicId) {
        return bookingRepository.findAllByFkListing(publicId)
                .stream()
                .map(bookingMapper::bookingToCheckAvailability).toList();
    }

    // Retrieves booked listings for the authenticated user
    public List<BookedListingDTO> getBookedListing() {
        // Get the currently authenticated user
        ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSecurityContext();

        // Fetch all bookings for the user by their public ID
        List<Booking> allBookings = bookingRepository.findAllByFkTenant(connectedUser.publicId());

        // Extract listing IDs from bookings
        List<UUID> allListingPublicIDs = allBookings.stream()
                .map(Booking::getFkListing)
                .toList();

        // Fetch listing details for the extracted listing IDs
        List<DisplayCardListingDTO> allListings = landlordService.getCardDisplayByListingPublicId(allListingPublicIDs);

        // Map bookings and listings to DTOs
        return mapBookingToBookedListingDTO(allBookings, allListings);
    }

    // Maps bookings and listings to BookedListingDTO objects
    private List<BookedListingDTO> mapBookingToBookedListingDTO(List<Booking> allBookings, List<DisplayCardListingDTO> allListings) {
        return allBookings.stream().map(booking -> {
            // Find the matching listing for the booking
            DisplayCardListingDTO displayListingDTO = allListings.stream()
                    .filter(listing -> listing.publicId().equals(booking.getFkListing()))
                    .findFirst()
                    .orElseThrow(); // Throws if no matching listing found

            // Convert booking to date DTO
            BookedDateDTO dates = bookingMapper.bookingToCheckAvailability(booking);

            // Create and return a new BookedListingDTO with relevant details
            return new BookedListingDTO(
                    displayListingDTO.cover(),
                    displayListingDTO.location(),
                    dates,
                    new PriceVO(booking.getTotalPrice()),
                    booking.getPublicId(),
                    displayListingDTO.publicId()
            );
        }).toList();
    }

    // cancel booking for user
    @Transactional(rollbackFor = Exception.class)
    public State<UUID, String> cancel(UUID bookingPublicId, UUID listingPublicId, boolean byLandlord) {
        // Get the currently authenticated user
        ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSecurityContext();
        int deleteSuccess = 0;

        if (SecurityUtils.hasCurrentUserAnyOfAuthorities(SecurityUtils.ROLE_LANDLORD) && byLandlord) {
            deleteSuccess = handleDeletionForLandlord(bookingPublicId, listingPublicId, connectedUser, deleteSuccess);
        }else {
            deleteSuccess =  bookingRepository.deleteBookingByFkTenantAndPublicId(connectedUser.publicId(),bookingPublicId);
        }
        if (deleteSuccess >=1){
            return State.<UUID, String>builder().forSuccess(bookingPublicId);
        }
        return State.<UUID, String>builder().forError("Booking not found");

    }

    /*
    * The function ensures that:
    The landlord attempting to delete the booking is authorized to do so (i.e., the listing belongs to the landlord).
    The booking is deleted if the authorization check passes
    * */
    private int handleDeletionForLandlord(UUID bookingPublicId, UUID listingPublicId, ReadUserDTO connectedUser, int deleteSuccess) {
        // Optional that may contain a DisplayCardListingDTO object if the listing exists and belongs to the landlord
        Optional<DisplayCardListingDTO> listingVerificationOpt = landlordService
                .getByPublicIdAndLandlordPublicId(listingPublicId, connectedUser.publicId());
        if (listingVerificationOpt.isPresent()) {
            deleteSuccess = bookingRepository.deleteBookingByPublicIdAndFkListing(bookingPublicId, listingVerificationOpt.get().publicId());
        }
        return deleteSuccess;

    }

    @Transactional(readOnly = true )
    public List<BookedListingDTO> getBookedListingForLandlord(){
        ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSecurityContext();
        List<DisplayCardListingDTO> allProperties =  landlordService.getAllProperties(connectedUser);
        List<UUID> allPropertyByPublicIds = allProperties.stream().map(DisplayCardListingDTO::publicId).toList();
        List<Booking> allBookings = bookingRepository.findAllByFkListing(connectedUser.publicId());
        return mapBookingToBookedListingDTO(allBookings, allProperties);

    }

    public List<UUID> getBookingMatchByListingIdsAndBookedDate(List<UUID> listingId, BookedDateDTO bookedDateDTO){
        return bookingRepository.findAllMatchWithDate(listingId, bookedDateDTO.startDate(), bookedDateDTO.endDate())
                .stream()
                .map(Booking::getFkListing)
                .toList();
    }

}
