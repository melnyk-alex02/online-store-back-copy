package com.store.service;

import com.store.constants.CodeType;
import com.store.dto.emailDTOs.EmailDTO;
import com.store.dto.userDTOs.UserDTO;
import com.store.dto.userDTOs.UserRegisterDTO;
import com.store.dto.userDTOs.UserUpdateDTO;
import com.store.entity.Code;
import com.store.exception.InvalidDataException;
import com.store.keycloak.KeycloakService;
import com.store.mapper.UserMapper;
import com.store.utils.EmailTemplateReader;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidKeyException;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final KeycloakService keycloakService;
    private final UserMapper userMapper;
    private final CodeService codeService;
    private final EmailService emailService;
    private final CurrentUserService currentUserService;

    public String getGoogleAuthUrl() {
        return keycloakService.getGoogleAuthUrl();
    }

    public AccessTokenResponse getAccessToken(String email, String password) {
        AccessTokenResponse accessTokenResponse = keycloakService.getToken(email, password);

        String emailText = EmailTemplateReader.readTemplateText("successful_login.txt");
        emailService.sendEmail(new EmailDTO(
                email,
                "Successful Login to Your Happy Tails Account",
                emailText
        ));

        return accessTokenResponse;
    }

    public AccessTokenResponse handleOauth2Code(String code) {
        return keycloakService.handleOauth2Code(code);
    }

    public UserDTO getUserByUuid(String userUuid) {
        UserRepresentation userRepresentation = keycloakService.getUserByUserUuid(userUuid);

        return userMapper.toDto(userRepresentation);
    }

    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return keycloakService.getAllUsers(pageable).map(userMapper::toDto);
    }

    public UserDTO getUserByEmail(String email) {
        return userMapper.toDto(keycloakService.getUserByEmail(email));
    }

    public UserDTO getCurrentUserInfoByUserId() {
        return userMapper.toDto(keycloakService.getCurrentUserInfoByUserId(currentUserService.getCurrentUserUuid()));
    }

    public AccessTokenResponse createUser(UserRegisterDTO userRegisterDTO) {
        return keycloakService.createUser(userRegisterDTO);
    }

    public UserDTO updateUser(UserUpdateDTO userUpdateDTO) {
        return userMapper.toDto(keycloakService.updateUser(userUpdateDTO, currentUserService.getCurrentUserUuid()));
    }

    public void deleteUserByUserId(String userId) {
        keycloakService.deleteUser(userId);
    }

    public void sendEmailForVerification() throws InvalidKeyException {
        String userId = currentUserService.getCurrentUserUuid();
        UserRepresentation userRepresentation = keycloakService.getUserByUserUuid(userId);

        if (userRepresentation.isEmailVerified()) {
            throw new InvalidDataException("Email is already verified");
        }

        Code code = codeService.createCode(
                userRepresentation.getEmail(),
                CodeType.EMAIL_VERIFICATION
        );

        String emailText = EmailTemplateReader.addCodeToTemplate(
                EmailTemplateReader.readTemplateText("email_verification_after_registration.txt"),
                code.getValue()
        );

        emailService.sendEmail(new EmailDTO(userRepresentation.getEmail(), "Email Verification", emailText));
    }

    public void verifyEmail(int code) {
        String userId = currentUserService.getCurrentUserUuid();

        UserRepresentation user = keycloakService.getUserByUserUuid(userId);
        try {
            codeService.validateCode(user.getEmail(), code, CodeType.EMAIL_VERIFICATION);
        } catch (InvalidDataException e) {
            throw new InvalidDataException("Check input for email verification");
        }

        keycloakService.verifyEmail(user);
    }

    public void sendEmailForPasswordlessLogin(String email) throws InvalidKeyException {
        if (!keycloakService.checkIfEmailVerified(email)) {
            throw new InvalidDataException("Email must be verified for passwordless login");
        }

        Code code = codeService.createCode(email, CodeType.PASSWORDLESS_LOGIN);

        String emailText = EmailTemplateReader.addCodeToTemplate(
                EmailTemplateReader.readTemplateText("request_passwordless_login.txt"),
                code.getValue()
        );

        emailService.sendEmail(new EmailDTO(
                email,
                "Login to Your Happy Tails Account",
                emailText
        ));
    }

    public AccessTokenResponse authenticateWithCode(String email, int code) {
        AccessTokenResponse accessTokenResponse = keycloakService.authenticateWithCode(email, code);

        String emailText = EmailTemplateReader.readTemplateText("successful_login.txt");
        emailService.sendEmail(new EmailDTO(
                email,
                "Successful Login to Your Happy Tails Account",
                emailText
        ));

        return accessTokenResponse;
    }

    public void sendEmailForResetPassword(String email) throws InvalidKeyException {
        keycloakService.deleteRequiredActions(email);

        Code code = codeService.createCode(email, CodeType.RESET_PASSWORD);

        String emailText = EmailTemplateReader.addCodeToTemplate(
                EmailTemplateReader.readTemplateText("request_password_change.txt"),
                code.getValue()
        );

        emailService.sendEmail(new EmailDTO(email, "Password Change Code for Happy Tails Online Store", emailText));
    }

    public void resetPassword(String email, int code, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new InvalidDataException("Password and Confirm Password does not match");
        }

        codeService.validateCode(email, code, CodeType.RESET_PASSWORD);

        keycloakService.updatePassword(email, newPassword);

        String emailText = EmailTemplateReader.readTemplateText("successful_password_change.txt");

        emailService.sendEmail(new EmailDTO(
                email,
                "Password Change Successful",
                emailText
        ));
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        return keycloakService.refreshToken(refreshToken);
    }

    public void logout() {
        keycloakService.logout(currentUserService.getCurrentUserUuid());
    }
}