package org.example.backend.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
// Marks this class as a configuration class for Spring Boot.
@EnableJpaRepositories({"org.example.backend.user.repository",
        "org.example.backend.listing.repository",
        "org.example.backend.booking.repository"
})
// Enables Spring Data JPA repositories in the specified package, allowing Spring to scan and manage JPA repository interfaces.
@EnableTransactionManagement
// Enables Spring's annotation-driven transaction management, allowing @Transactional to work properly.
@EnableJpaAuditing
// Enables JPA Auditing, which allows automatic tracking of entity changes (e.g., @CreatedDate, @LastModifiedDate).
public class DatabaseConfiguration {
}
