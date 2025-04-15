package org.example.backend.listing.presentation;

import jakarta.validation.Valid;
import org.example.backend.listing.application.TenantService;
import org.example.backend.listing.application.dto.DisplayCardListingDTO;
import org.example.backend.listing.application.dto.DisplayListingDTO;
import org.example.backend.listing.application.dto.SearchDTO;
import org.example.backend.listing.domain.BookingCategory;
import org.example.backend.sharekernel.service.State;
import org.example.backend.sharekernel.service.StatusNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tenant-listing")
public class TenantResource {

    private final TenantService tenantService;

    @Autowired
    public TenantResource(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/get-all-by-category")
    public ResponseEntity<Page<DisplayCardListingDTO>> finaAllByBookingCategory(Pageable pageable,
                                                                                @RequestParam BookingCategory bookingCategory) {
        return ResponseEntity.ok(tenantService.getAllByCategory(pageable, bookingCategory));
    }


    @GetMapping("/get-one")
    public ResponseEntity<DisplayListingDTO> getOne(@RequestParam UUID publicId) {
        State<DisplayListingDTO, String> displayListingState = tenantService.getOne(publicId);
        if (displayListingState.getStatus().equals(StatusNotification.OK)) {
            return ResponseEntity.ok(displayListingState.getValue());
        }
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, displayListingState.getError());
        return ResponseEntity.of(problemDetail).build();
    }

    public ResponseEntity<Page<DisplayCardListingDTO>> search(Pageable pageable,
                                                              @Valid @RequestBody SearchDTO searchDTO){
        return ResponseEntity.ok(tenantService.search(pageable, searchDTO));
    }


}
