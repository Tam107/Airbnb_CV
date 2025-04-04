package org.example.backend.listing.presentation;

import lombok.RequiredArgsConstructor;
import org.example.backend.listing.application.LandlordService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/landlord-listing")
@RequiredArgsConstructor
public class LandlordResource {

    private final LandlordService landlordService;

}
