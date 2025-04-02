package org.example.backend.listing.application.dto.vo;


import jakarta.validation.constraints.NotNull;

/*represent information about bathroom-related features*/
/*
 * the VO(Value Object) folder
 * @purpose
 * Represent immutable objects that describe characteristics or states
 * Encapsulates business rules and validation for simple data types
 * Used to represent concepts with no identity, just their attributes matter
 *
 */
public record BathsVO(@NotNull(message = "Bath must be present") int value) {


}
