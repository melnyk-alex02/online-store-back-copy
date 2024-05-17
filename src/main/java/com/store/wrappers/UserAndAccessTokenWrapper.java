package com.store.wrappers;

import com.store.dto.userDTOs.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.keycloak.representations.AccessTokenResponse;

@Getter
@Setter
@AllArgsConstructor
public class UserAndAccessTokenWrapper {
    private AccessTokenResponse accessTokenResponse;
    private UserDTO userDTO;
}