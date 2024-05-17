package com.store.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.constants.Role;
import com.store.dto.userDTOs.UserRegisterDTO;
import com.store.dto.userDTOs.UserUpdateDTO;
import com.store.exception.ConflictException;
import com.store.exception.DataNotFoundException;
import com.store.exception.InvalidDataException;
import com.store.webconfig.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;

import static com.store.keycloak.CredentialsUtils.createOtp;
import static com.store.keycloak.CredentialsUtils.createPasswordCredentials;

@Service
@RequiredArgsConstructor
public class KeycloakService {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakService.class);
    private final ApplicationProperties applicationProperties;
    private final KeycloakClientFactory keycloakClientFactory;
    private final RestTemplate restTemplate;

    public String getGoogleAuthUrl() {
        String redirectUri = "http://localhost:4999/happytails/api/login/oauth2/code/google";

        return "https://accounts.google.com/o/oauth2/auth"
                + "?client_id=" + this.applicationProperties.getKeycloak().getGoogleClientId()
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=email profile openid";
    }

    public AccessTokenResponse handleOauth2Code(String code) {
        String accessToken = exchangeCodeForToken(code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        requestBody.add("subject_token", accessToken);
        requestBody.add("subject_token_type", "urn:ietf:params:oauth:token-type:access_token");
        requestBody.add("subject_issuer", "google");
        requestBody.add("client_id", this.applicationProperties.getKeycloak().getClientId());
        requestBody.add("scope", this.applicationProperties.getKeycloak().getScopes());
        requestBody.add("client_secret", this.applicationProperties.getKeycloak().getClientSecret());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        String exchangeUrl = this.applicationProperties.getKeycloak().getBaseUrl() + "/realms/"
                + this.applicationProperties.getKeycloak().getRealm()
                + "/protocol/openid-connect/token";

        ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
                exchangeUrl,
                HttpMethod.POST,
                entity,
                AccessTokenResponse.class);

        return response.getBody();
    }

    public AccessTokenResponse getToken(String userEmail, String password) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "password");
        requestBody.add("client_id", this.applicationProperties.getKeycloak().getClientId());
        requestBody.add("client_secret", this.applicationProperties.getKeycloak().getClientSecret());
        requestBody.add("username", userEmail);
        requestBody.add("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);


        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl =
                this.applicationProperties.getKeycloak().getBaseUrl() + "/realms/"
                        + this.applicationProperties.getKeycloak().getRealm()
                        + "/protocol/openid-connect/token";
        ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                entity,
                AccessTokenResponse.class
        );

        return response.getBody();
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "refresh_token");
        requestBody.add("client_id", this.applicationProperties.getKeycloak().getClientId());
        requestBody.add("client_secret", this.applicationProperties.getKeycloak().getClientSecret());
        requestBody.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        String refreshTokenUrl = "http://"
                + this.applicationProperties.getKeycloak().getBaseUrl() + "/realms/"
                + this.applicationProperties.getKeycloak().getRealm()
                + "/protocol/openid-connect/token";
        ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
                refreshTokenUrl,
                HttpMethod.POST,
                entity,
                AccessTokenResponse.class);

        return response.getBody();
    }

    private RealmResource realmResource() {
        return keycloakClientFactory.getInstance()
                .realm(applicationProperties.getKeycloak().getRealm());
    }

    private UsersResource usersResource() {
        return keycloakClientFactory.getInstance()
                .realm(applicationProperties.getKeycloak().getRealm())
                .users();
    }

    public List<String> getUserRoles(String userUuid) {
        return usersResource()
                .get(userUuid)
                .roles()
                .realmLevel()
                .listAll()
                .stream()
                .map(RoleRepresentation::getName)
                .toList();
    }

    private void addRolesToUser(List<RoleRepresentation> realmRoles, String userUuid) {
        usersResource().get(userUuid).roles().realmLevel().add(realmRoles);
    }

    private RoleRepresentation getRole(String role) {
        return realmResource().roles().get(role).toRepresentation();
    }

    public UserRepresentation getUserByUserUuid(String userUuid) {
        UserRepresentation userRepresentation = usersResource().get(userUuid).toRepresentation();
        userRepresentation.setRealmRoles(getUserRoles(userUuid));
        return userRepresentation;
    }

    public Page<UserRepresentation> getAllUsers(Pageable pageable) {
        return new PageImpl<>(usersResource().list(), pageable, usersResource().count());
    }

    public UserRepresentation getCurrentUserInfoByUserId(String userId) {
        UserRepresentation userRepresentation = usersResource().get(userId).toRepresentation();
        userRepresentation.setRealmRoles(getUserRoles(userId));
        return userRepresentation;
    }

    public void logout(String userId) {
        usersResource().get(userId).logout();
    }

    public AccessTokenResponse createUser(UserRegisterDTO userRegisterDTO) {
        if (!Objects.equals(userRegisterDTO.getPassword(), userRegisterDTO.getConfirmPassword())) {
            throw new InvalidDataException("Password and Confirm Password does not match");
        }
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setAttributes(userRegisterDTO.getAttributes());
        userRepresentation.setEmail(userRegisterDTO.getEmail());
        userRepresentation.setFirstName(userRegisterDTO.getFirstName());
        userRepresentation.setLastName(userRegisterDTO.getLastName());
        userRepresentation.setCredentials(List.of(createPasswordCredentials(userRegisterDTO.getPassword())));
        userRepresentation.setEnabled(true);

        Response response = usersResource().create(userRepresentation);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());

        switch (httpStatus) {
            case CREATED -> logger.info("User with email " + userRegisterDTO.getEmail() + " was created");
            case CONFLICT ->
                    throw new ConflictException("User with email: " + userRegisterDTO.getEmail() + " already exists");
            case BAD_REQUEST -> throw new InvalidDataException("Cannot create user, check input data");
            default ->
                    throw new ResponseStatusException(httpStatus, "Problems occurred while creating user with email" + userRegisterDTO.getEmail());
        }

        UserRepresentation user = usersResource().search(userRegisterDTO.getEmail(), true).get(0);
        userRepresentation.setId(user.getId());
        userRepresentation.setCreatedTimestamp(user.getCreatedTimestamp());
        addRolesToUser(List.of(getRole(Role.USER)), user.getId());
        userRepresentation.setRealmRoles(getUserRoles(user.getId()));

        return getToken(userRegisterDTO.getEmail(), userRegisterDTO.getPassword());
    }

    public void updatePassword(String email, String password) {
        UserRepresentation user = usersResource().search(email, true).get(0);
        user.setRequiredActions(List.of());
        user.setCredentials(List.of(createPasswordCredentials(password)));
        usersResource().get(user.getId()).update(user);
    }

    public void verifyEmail(UserRepresentation user) {
        if (user.isEmailVerified()) {
            throw new InvalidDataException("Email already verified");
        }

        user.setEmailVerified(true);

        usersResource().get(user.getId()).update(user);
    }

    public AccessTokenResponse authenticateWithCode(String email, int otp) {
        if (usersResource().search(email, true).isEmpty()) {
            throw new DataNotFoundException("There is no user with email" + email);
        }
        UserRepresentation user = usersResource().search(email, true).get(0);

        user.setCredentials(List.of(createOtp(otp)));
        usersResource().get(user.getId()).update(user);

        AccessTokenResponse accessTokenResponse = getToken(email, String.valueOf(otp));

        user.setRequiredActions(List.of("UPDATE_PASSWORD"));
        usersResource().get(user.getId()).update(user);

        return accessTokenResponse;
    }

    public UserRepresentation getUserByEmail(String email) {
        if (usersResource().search(email, true).isEmpty()) {
            throw new DataNotFoundException("There is no user with email " + email);
        }

        UserRepresentation user = usersResource().search(email, true).get(0);
        user.setRealmRoles(getUserRoles(user.getId()));
        return user;
    }

    public void deleteRequiredActions(String email) {
        UserRepresentation user = getUserByEmail(email);
        if (!user.getRequiredActions().isEmpty() && user.getRequiredActions().get(0).equals("UPDATE_PASSWORD")) {
            user.setRequiredActions(List.of());
            usersResource().get(user.getId()).update(user);
        }
    }

    public boolean checkIfEmailVerified(String email) {
        if (usersResource().search(email, true).isEmpty()) {
            throw new DataNotFoundException("There is no user with email " + email);
        }

        UserRepresentation user = usersResource().search(email, true).get(0);
        return user.isEmailVerified();
    }

    public void deleteUser(String userId) {
        usersResource().get(userId).remove();
    }

    public UserRepresentation updateUser(UserUpdateDTO userUpdateDTO, String userId) {
        UserRepresentation userRepresentation = getUserByUserUuid(userId);

        if (!userRepresentation.isEmailVerified()) {
            throw new InvalidDataException("Email must be verified for user update");
        }

        userRepresentation.setEmail(userUpdateDTO.getEmail());
        userRepresentation.setFirstName(userUpdateDTO.getFirstName());
        userRepresentation.setLastName(userUpdateDTO.getLastName());
        userRepresentation.setAttributes(userUpdateDTO.getAttributes());

        usersResource().get(userId).update(userRepresentation);

        return userRepresentation;
    }

    private String exchangeCodeForToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "320287221695-83tuus2agus0o9tgsmr19935tvad32lo.apps.googleusercontent.com");
        body.add("client_secret", "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");
        body.add("redirect_uri", "http://localhost:4999/happytails/api/login/oauth2/code/google");
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("https://oauth2.googleapis.com/token", request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        String accessToken;
        try {
            rootNode = mapper.readTree(response.getBody());
            accessToken = rootNode.path("access_token").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
        return accessToken;
    }
}