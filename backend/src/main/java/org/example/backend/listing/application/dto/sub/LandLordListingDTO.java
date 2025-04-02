package org.example.backend.listing.application.dto.sub;

import jakarta.validation.constraints.NotNull;

public record LandLordListingDTO(
        @NotNull String firstname,
        @NotNull String imageUrl
) {
}
