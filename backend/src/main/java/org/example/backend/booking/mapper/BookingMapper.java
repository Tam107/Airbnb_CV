package org.example.backend.booking.mapper;

import org.example.backend.booking.application.dto.BookedDateDTO;
import org.example.backend.booking.application.dto.NewBookingDTO;
import org.example.backend.booking.domain.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    // No specific mapping methods defined yet, but can be added in the future
    // if needed for converting between Booking entities and DTOs.

    Booking newBookingToBooking(NewBookingDTO newBookingDTO);

    // check the booking date valid
    BookedDateDTO bookingToCheckAvailability(Booking booking);
}