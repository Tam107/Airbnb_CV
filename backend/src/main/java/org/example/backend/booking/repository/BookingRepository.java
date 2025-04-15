package org.example.backend.booking.repository;

import org.example.backend.booking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Query("SELECT case when count(booking) > 0 then true else false end from Booking booking" +
            " where not (booking.endDate <= :startDate" +
            " or booking.startDate >= :endDate) and booking.fkListing = :fkListing")
    boolean bookingExistsAtInterval(
            OffsetDateTime startDate, OffsetDateTime endDate,
            UUID fkListing
    );

    /**
     * Retrieve All Bookings for a Listing
     * Check Booking History
     * Availability Calculations*/
    List<Booking> findAllByFkListing(UUID fkListing);

    List<Booking> findAllByFkTenant(UUID fkTenant);

    int deleteBookingByPublicIdAndFkListing(UUID bookingPublicId, UUID uuid);

    int deleteBookingFkTenantAndPublicId(UUID uuid, UUID bookingPublicId);
}
