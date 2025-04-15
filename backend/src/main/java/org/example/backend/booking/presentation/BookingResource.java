package org.example.backend.booking.presentation;

import jakarta.validation.Valid;
import org.example.backend.booking.application.BookingService;
import org.example.backend.booking.application.dto.BookedDateDTO;
import org.example.backend.booking.application.dto.BookedListingDTO;
import org.example.backend.booking.application.dto.NewBookingDTO;
import org.example.backend.sharekernel.service.State;
import org.example.backend.sharekernel.service.StatusNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/booking")
public class BookingResource {

    private final BookingService bookingService;

    @Autowired
    public BookingResource(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("create")
    public ResponseEntity<Boolean> create(@Valid @RequestBody NewBookingDTO newBookingDTO){
        State<Void, String> createState = bookingService.create(newBookingDTO);
        if (createState.getStatus().equals(StatusNotification.ERROR)){
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST, createState.getError()
            );
            return ResponseEntity.of(problemDetail).build();
        }else {
            return ResponseEntity.ok(true);
        }
    }

    @GetMapping("check-availability")
    public ResponseEntity<List<BookedDateDTO>> checkAvailability(@RequestParam UUID listingPublicId){
        return ResponseEntity.ok(bookingService.checkAvailability(listingPublicId));
    }

    @GetMapping("get-booked-listing")
    public ResponseEntity<List<BookedListingDTO>> getBookedListing(){
        return ResponseEntity.ok(bookingService.getBookedListing());
    }

    public ResponseEntity<UUID> cancel(@RequestParam UUID bookingPublicId,
                                       @RequestParam UUID listingPublicId,
                                       @RequestParam boolean byLandlord){
        State<UUID, String> cancelState = bookingService.cancel(bookingPublicId, listingPublicId, byLandlord);
        if (cancelState.getStatus().equals(StatusNotification.ERROR)){
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, cancelState.getError());
            return ResponseEntity.of(problemDetail).build();
        }
        return ResponseEntity.ok(bookingPublicId);
    }

}
