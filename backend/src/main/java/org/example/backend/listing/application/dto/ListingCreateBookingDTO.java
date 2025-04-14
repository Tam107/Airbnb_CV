package org.example.backend.listing.application.dto;

import org.example.backend.listing.application.dto.vo.PriceVO;

import java.util.UUID;

public record ListingCreateBookingDTO(
        UUID listingPublicId,
        PriceVO price
) {
}
