package org.example.backend.listing.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.example.backend.booking.application.dto.BookedDateDTO;
import org.example.backend.listing.application.dto.sub.ListingInfoDTO;

public record SearchDTO(
        @Valid BookedDateDTO dates,
        @Valid ListingInfoDTO infos,
        @NotEmpty String location
) {
}
