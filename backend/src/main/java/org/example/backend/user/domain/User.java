package org.example.backend.user.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.example.backend.sharekernel.domain.AbstractAuditingEntity;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "airbnb_user")
@Data
public class User extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSequenceGenerator")
    @SequenceGenerator(name = "userSequenceGenerator", sequenceName = "user_generator", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "public_id", nullable = false)
    private UUID publicId;


    @ManyToMany
    @JoinTable(name = "user_authority",
    joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
    inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")}
    )
    private Set<Authority> authorities = new HashSet<>();


    @Override
    public Long getId() {
        return id;
    }
}
