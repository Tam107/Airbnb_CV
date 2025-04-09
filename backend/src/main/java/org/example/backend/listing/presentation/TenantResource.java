package org.example.backend.listing.presentation;

import org.example.backend.listing.application.TenantService;
import org.example.backend.listing.application.dto.DisplayCardListingDTO;
import org.example.backend.listing.domain.BookingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenant-listing")
public class TenantResource {

    private final TenantService tenantService;

    @Autowired
    public TenantResource(TenantService tenantService){
        this.tenantService = tenantService;
    }

    @GetMapping("/get-all-by-category")
    public ResponseEntity<Page<DisplayCardListingDTO>> finaAllByBookingCategory(Pageable pageable,
                                                                                @RequestParam BookingCategory bookingCategory){
        return ResponseEntity.ok(tenantService.getAllByCategory(pageable, bookingCategory));
    }

}
