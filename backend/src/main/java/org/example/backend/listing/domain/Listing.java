package org.example.backend.listing.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.example.backend.sharekernel.domain.AbstractAuditingEntity;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "listing")
@Data
public class Listing extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "listingSequenceGenerator")
    @SequenceGenerator(name = "listingSequenceGenerator", sequenceName = "listing_generator", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @UuidGenerator
    @Column(name = "public_id", nullable = false)
    private UUID publicId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "guests")
    private String guests;

    @Column(name = "beds")
    private int beds;

    @Column(name = "bathrooms")
    private String bathrooms;

    @Column(name = "price")
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private BookingCategory bookingCategory;

    @Column(name = "location")
   private String location;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.REMOVE)
    private Set<ListingPicture> pictures = new HashSet<>();

    @Override
    public Long getId() {
        return id;
    }
}
