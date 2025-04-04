package org.example.backend.user.application;

import lombok.RequiredArgsConstructor;
import org.example.backend.infrastructure.config.SecurityUtils;
import org.example.backend.user.application.dto.ReadUserDTO;
import org.example.backend.user.domain.User;
import org.example.backend.user.mapper.UserMapper;
import org.example.backend.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    // key used to check when a user was last updated in the identity provider
    private static final String UPDATED_AT_KEY ="updated_at";

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    /**
    * Gets the currently authenticated user from the Spring security context
    * @return User DTO
    * */
    /*return UUID publicId,
        String firstName,
        String lastName,
        String email,
        String imageUrl,
        Set<String> authorities*/
    @Transactional
    public ReadUserDTO getAuthenticatedUserFromSecurityContext(){
        // Get OAuth2 user from security context
        OAuth2User principal = (OAuth2User) SecurityContextHolder.getContext();
        // Convert Oauth2 attributes to user entity
        User user = SecurityUtils.mapOauth2AttributesToUser(principal.getAttributes());

        //find and return the user by email
        return getByEmail(user.getEmail()).orElseThrow();
    }

    /**
    * @param email User's email address
    * @return Optional containing user DTO if found
    * */

    @Transactional
    public Optional<ReadUserDTO> getByEmail(String email) {
        Optional<User> oneByEmail = userRepository.findOneByEmail(email);
        return oneByEmail.map(userMapper::readUserDTOToUser);
    }

    /**
    * synchronizes your application's user data with data from an external identity provider (like Google, Microsoft, etc.)
    * @param oAuth2User User data from OAuth2 provider
    * @param forceResync whether to force synchronization regardless of modified date
    * */

    public void  syncWithIdp(OAuth2User oAuth2User, boolean forceResync){
        // Get user attributes from OAuth2
        Map<String, Object> attributes = oAuth2User.getAttributes();

//      Map OAuth2 attributes to User entity
        User user = SecurityUtils.mapOauth2AttributesToUser(attributes);
        Optional<User> existingUser = userRepository.findOneByEmail(user.getEmail());

        if (existingUser.isPresent()){
            // check if we have time stamp from identity provider
            if (attributes.get(UPDATED_AT_KEY) !=null){
                // get last modified date from database
                Instant lastModifiedDate = existingUser.orElseThrow().getLastModifiedDate();
                // "IdP" stands for "Identity Provider."
                Instant idpModifiedDate;

                // handle different timestamp format
                if (attributes.get(UPDATED_AT_KEY) instanceof Instant instant){
                    idpModifiedDate = instant;
                }else {
                    idpModifiedDate = Instant.ofEpochSecond((Integer) attributes.get(UPDATED_AT_KEY));
                }

                // update if idp data is newer or force resync is true
                if (idpModifiedDate.isAfter(lastModifiedDate) || forceResync){
                    updateUser(user);
                }
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
