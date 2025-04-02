package org.example.backend.listing.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.backend.listing.application.dto.sub.DescriptionDTO;
import org.example.backend.listing.application.dto.sub.ListingInfoDTO;
import org.example.backend.listing.application.dto.sub.PictureDTO;
import org.example.backend.listing.application.dto.vo.PriceVO;
import org.example.backend.listing.domain.BookingCategory;

import java.util.List;

@Data
public class SaveListingDTO {

    @NotNull
    BookingCategory category;

    @NotNull
    String location;

    @Valid
    @NotNull
    ListingInfoDTO infos;

    @Valid
    @NotNull
    DescriptionDTO description;

    @Valid
    @NotNull
    PriceVO price;

    @NotNull
    List<PictureDTO> pictures;



}
