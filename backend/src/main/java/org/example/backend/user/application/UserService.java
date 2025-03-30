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

    private static final String UPDATED_AT_KEY ="updated_at";

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public ReadUserDTO getAuthenticatedUserFromSecurityContext(){
        OAuth2User principal = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = SecurityUtils.mapOauth2AttributesToUser(principal.getAttributes());
        return getByEmail(user.getEmail()).orElseThrow();
    }

    @Transactional
    public Optional<ReadUserDTO> getByEmail(String email) {
        Optional<User> oneByEmail = userRepository.findOneByEmail(email);
        return oneByEmail.map(userMapper::readUserDTOToUser);
    }

    public void syncWithIdp(OAuth2User oAuth2User, boolean forceResync){
        Map<String, Object> attributes = oAuth2User.getAttributes();
        User user = SecurityUtils.mapOauth2AttributesToUser(attributes);
        Optional<User> existingUser = userRepository.findOneByEmail(user.getEmail());
        if (existingUser.isPresent()){
            if (attributes.get(UPDATED_AT_KEY) != null){
                Instant lastModifiedDate = existingUser.orElseThrow().getLastModifiedDate();
                Instant idpModifiedDate;
                if (attributes.get(UPDATED_AT_KEY) instanceof  Instant instant){
                    idpModifiedDate = instant;
                }else {
                    idpModifiedDate = Instant.ofEpochSecond((Integer) attributes.get(UPDATED_AT_KEY));
                }
                if (idpModifiedDate.isAfter(lastModifiedDate) || forceResync){
                    updateUser(user);
                }
            }
            else {
                userRepository.saveAndFlush(user);
            }
        }
    }

    private void updateUser(User user) {
       Optional<User> userToUpdateOpt = userRepository.findOneByEmail(user.getEmail());
       if (userToUpdateOpt.isPresent()){
          User userToUpdate =   userToUpdateOpt.get();
          userToUpdate.setEmail(user.getEmail());
          userToUpdate.setFirstName(user.getFirstName());
          userToUpdate.setLastName(user.getLastName());
          userToUpdate.setAuthorities(user.getAuthorities());
          userToUpdate.setImageUrl(user.getImageUrl());

          userRepository.saveAndFlush(userToUpdate);
       }
    }

    public Optional<ReadUserDTO> getByPublicId(UUID publicId){
        Optional<User> oneByPublicId = userRepository.findOneByPublicId(publicId);
        return oneByPublicId.map(userMapper::readUserDTOToUser);
    }
}
