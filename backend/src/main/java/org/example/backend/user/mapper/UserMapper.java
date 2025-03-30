package org.example.backend.user.mapper;

import org.example.backend.user.application.dto.ReadUserDTO;
import org.example.backend.user.domain.Authority;
import org.example.backend.user.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ReadUserDTO readUserDTOToUser(User user);

    default String mapAuthoritiesToString(Authority authority){
        return authority.getName();
    }

}
