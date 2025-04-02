package org.example.backend.listing.application.dto.sub;

import jakarta.validation.constraints.NotNull;
import org.example.backend.listing.application.dto.vo.DescriptionVO;
import org.example.backend.listing.application.dto.vo.TitleVO;

/*
* the sub(subdomain) folder
* @purpose
* Organizes DTOs related to specific subdomains
* Helps break down complex domains into smaller, more manageable parts
* supports modular and clean architecture
*/

public record DescriptionDTO(
        @NotNull TitleVO title,
        @NotNull DescriptionVO description) {
}
