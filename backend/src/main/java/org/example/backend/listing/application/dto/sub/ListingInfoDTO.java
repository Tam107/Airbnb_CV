package org.example.backend.listing.application.dto.sub;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.backend.listing.application.dto.vo.BathsVO;
import org.example.backend.listing.application.dto.vo.BedroomsVO;
import org.example.backend.listing.application.dto.vo.BedsVO;
import org.example.backend.listing.application.dto.vo.GuestVO;

public record ListingInfoDTO(@NotNull @Valid GuestVO guest,
                             @NotNull @Valid BedroomsVO bedrooms,
                             @NotNull @Valid BedsVO beds,
                             @NotNull @Valid BathsVO baths) {


}
