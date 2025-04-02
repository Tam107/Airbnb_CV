package org.example.backend.listing.application.dto.vo;

import jakarta.validation.constraints.NotNull;

/*represent bedrooms value object*/
public record BedroomsVO(@NotNull(message = "Bedroom value must be present")int value) {
}
