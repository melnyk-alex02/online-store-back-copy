package com.store.mapper;

import com.store.dto.userDTOs.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@Mapper
public interface UserMapper {
    @Mapping(source = "createdTimestamp", target = "registerDate")
    @Mapping(source = "id", target = "userId")
    @Mapping(source = "realmRoles", target = "roles")
    UserDTO toDto(UserRepresentation userRepresentation);

    @Mapping(source = "createdTimestamp", target = "registerDate")
    @Mapping(source = "id", target = "userId")
    @Mapping(source = "realmRoles", target = "roles")
    List<UserDTO> toDto(List<UserRepresentation> userRepresentations);

    default LocalDateTime toLocalDateTime(Long createdTimestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(createdTimestamp), TimeZone.getDefault().toZoneId());
    }
}