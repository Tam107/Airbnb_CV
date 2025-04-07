package org.example.backend.listing.application.dto;

import org.example.backend.listing.application.dto.sub.PictureDTO;
import org.example.backend.listing.application.dto.vo.PriceVO;
import org.example.backend.listing.domain.BookingCategory;

import java.util.UUID;

// add price value object
public record DisplayCardListingDTO(
        PriceVO price,
        String location,
        PictureDTO cover,
        BookingCategory bookingCategory,
        UUID publicId) {
}
