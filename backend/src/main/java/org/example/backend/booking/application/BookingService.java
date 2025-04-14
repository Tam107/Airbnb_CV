package org.example.backend.booking.application;

import org.example.backend.booking.application.dto.NewBookingDTO;
import org.example.backend.booking.mapper.BookingMapper;
import org.example.backend.booking.repository.BookingRepository;
import org.example.backend.listing.application.LandlordService;
import org.example.backend.sharekernel.service.State;
import org.example.backend.user.application.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public State<Void, String> create(NewBookingDTO newBookingDTO){



        return null;
    }


}
