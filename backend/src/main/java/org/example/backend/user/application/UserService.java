package org.example.backend.user.application;

import lombok.RequiredArgsConstructor;
import org.example.backend.infrastructure.config.SecurityUtils;
import org.example.backend.user.application.dto.ReadUserDTO;
import org.example.backend.user.domain.User;
import org.example.backend.user.mapper.UserMapper;
import org.example.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing user data and synchronizing with identity provider
 */
@Service
public class UserService {

    // Key used to check when a user was last updated in the identity provider
    private static final String UPDATED_AT_KEY ="updated_at";

    // Dependencies automatically injected by Spring via constructor
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Gets the currently authenticated user from Spring Security context
     * @return User data transfer object
     */
    @Transactional
    public ReadUserDTO getAuthenticatedUserFromSecurityContext(){
        // Get OAuth2 user from security context
        OAuth2User principal = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Convert OAuth2 attributes to User entity
        User user = SecurityUtils.mapOauth2AttributesToUser(principal.getAttributes());
        // Find and return the user by email
        return getByEmail(user.getEmail()).orElseThrow();
    }

    /**
     * Finds a user by email and returns as DTO
     * @param email User's email address
     * @return Optional containing user DTO if found
     */
    @Transactional
    public Optional<ReadUserDTO> getByEmail(String email) {
        Optional<User> oneByEmail = userRepository.findOneByEmail(email);
        return oneByEmail.map(userMapper::readUserDTOToUser);
    }

    /**
     * Synchronizes local user data with identity provider data
     * @param oAuth2User User data from OAuth2 provider
     * @param forceResync Whether to force synchronization regardless of modification dates
     */
    public void syncWithIdp(OAuth2User oAuth2User, boolean forceResync){
        // Get user attributes from OAuth2
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // Map OAuth2 attributes to User entity
        User user = SecurityUtils.mapOauth2AttributesToUser(attributes);
        // Check if user already exists in database
        Optional<User> existingUser = userRepository.findOneByEmail(user.getEmail());

        if (existingUser.isPresent()){
            // Check if we have update timestamp from identity provider
            if (attributes.get(UPDATED_AT_KEY) != null){
                // Get last modified date from database
                Instant lastModifiedDate = existingUser.orElseThrow().getLastModifiedDate();
                Instant idpModifiedDate;

                // Handle different timestamp formats
                if (attributes.get(UPDATED_AT_KEY) instanceof Instant instant){
                    idpModifiedDate = instant;
                } else {
                    idpModifiedDate = Instant.ofEpochSecond((Integer) attributes.get(UPDATED_AT_KEY));
                }

                // Update if IdP data is newer or force resync is true
                if (idpModifiedDate.isAfter(lastModifiedDate) || forceResync){
                    updateUser(user);
                }
            }
            else {
                // No timestamp available, always save
                userRepository.saveAndFlush(user);
            }
        }
    }

    /**
     * Updates existing user record with new data
     * @param user User entity with updated information
     */
    private void updateUser(User user) {
        Optional<User> userToUpdateOpt = userRepository.findOneByEmail(user.getEmail());
        if (userToUpdateOpt.isPresent()){
            User userToUpdate = userToUpdateOpt.get();
            // Update fields from identity provider
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setFirstName(user.getFirstName());
            userToUpdate.setLastName(user.getLastName());
            userToUpdate.setAuthorities(user.getAuthorities());
            userToUpdate.setImageUrl(user.getImageUrl());

            userRepository.saveAndFlush(userToUpdate);
        }
    }

    /**
     * Finds a user by their public ID
     * @param publicId User's public UUID
     * @return Optional containing user DTO if found
     */
    public Optional<ReadUserDTO> getByPublicId(UUID publicId){
        Optional<User> oneByPublicId = userRepository.findOneByPublicId(publicId);
        return oneByPublicId.map(userMapper::readUserDTOToUser);
    }
}