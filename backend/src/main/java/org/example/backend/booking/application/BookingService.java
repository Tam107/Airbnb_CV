package org.example.backend.booking.application;

import org.example.backend.booking.application.dto.BookedDateDTO;
import org.example.backend.booking.application.dto.NewBookingDTO;
import org.example.backend.booking.domain.Booking;
import org.example.backend.booking.mapper.BookingMapper;
import org.example.backend.booking.repository.BookingRepository;
import org.example.backend.listing.application.LandlordService;
import org.example.backend.listing.application.dto.ListingCreateBookingDTO;
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
    public State<Void, String> create(NewBookingDTO newBookingDTO){
        Booking booking = bookingMapper.newBookingToBooking(newBookingDTO);
        Optional<ListingCreateBookingDTO> listingOpt = landlordService.getByListingPublicId(newBookingDTO.listingPublicId());

        if (listingOpt.isEmpty()){
            return State.<Void, String>builder().forError("Landlord public id is not found");
        }
        boolean alreadyBooked = bookingRepository.bookingExistsAtInterval(newBookingDTO.startDate(), newBookingDTO.endDate(), newBookingDTO.listingPublicId());

        if (alreadyBooked){
            return State.<Void, String>builder().forError("This listing is already booked for the selected dates");
        }

        ListingCreateBookingDTO listingCreateBookingDTO = listingOpt.get();

        booking.setFkListing(listingOpt.get().listingPublicId());

        ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSecurityContext();
        booking.setFkTenant(connectedUser.publicId());
        booking.setNumberOfTravelers(1);

        long numberOfNights = ChronoUnit.DAYS.between(newBookingDTO.startDate(), newBookingDTO.endDate());
        booking.setTotalPrice( (int) (numberOfNights * listingCreateBookingDTO.price().value()));

        bookingRepository.save(booking);
        return State.<Void, String>builder().forSuccess();
    }


    @Transactional(readOnly = true)
    public List<BookedDateDTO> checkAvailability(UUID publicId){
        return bookingRepository.findAllByFkListing(publicId)
                .stream()
                .map(bookingMapper::bookingToCheckAvailability).toList();
    }

}
