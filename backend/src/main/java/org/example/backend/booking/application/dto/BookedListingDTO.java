package org.example.backend.booking.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.backend.listing.application.dto.sub.PictureDTO;
import org.example.backend.listing.application.dto.vo.PriceVO;

import java.util.UUID;

public record BookedListingDTO(
        @Valid PictureDTO cover,
        @NotEmpty String location,
        @Valid BookedDateDTO dates,
        @Valid PriceVO totalPrice,
        @NotNull UUID bookingPublicId,
        @NotNull UUID listingPublicId
        ) {
}
