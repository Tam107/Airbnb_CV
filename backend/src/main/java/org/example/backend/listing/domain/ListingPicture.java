package org.example.backend.listing.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.example.backend.sharekernel.domain.AbstractAuditingEntity;

@Entity
@Table(name = "listing_picture")
@Data
public class ListingPicture extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "listingSequenceGenerator")
    @SequenceGenerator(name = "listingSequenceGenerator", sequenceName = "listing_generator", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "listing_fk", referencedColumnName = "id")
    private Listing listing;

    @Lob
    @Column(name = "file",nullable = false)
    private byte[] file;

    @Column(name = "file_content_type")
    private String fileContentType;

    @Column(name = "cover")
    private boolean isCover;

    @Override
    public Long getId() {
        return id;
    }


}
