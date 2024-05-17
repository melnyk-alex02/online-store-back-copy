package com.store.restcontroller;

import com.store.constants.Role;
import com.store.dto.userDTOs.UserDTO;
import com.store.dto.userDTOs.UserRegisterDTO;
import com.store.dto.userDTOs.UserUpdateDTO;
import com.store.service.UserService;
import com.store.utils.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/login")
    public ResponseEntity<?> socialLoginWithGoogleIdp() {
        String googleAuthUrl = userService.getGoogleAuthUrl();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, googleAuthUrl)
                .build();
    }

    @GetMapping("/login/oauth2/code/google")
    public UserDTO handleOAuth2Code(@RequestParam("code") String code, HttpServletResponse httpServletResponse) throws IOException {
        AccessTokenResponse accessTokenResponse = userService.handleOauth2Code(code);

        httpServletResponse.addCookie(CookieUtils.createAccessTokenCookie(accessTokenResponse.getToken()));
        httpServletResponse.addCookie(CookieUtils.createRefreshTokenCookie(accessTokenResponse.getRefreshToken()));

        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Set-Cookie");

        UserDTO userDTO = userService.getCurrentUserInfoByUserId();

        logger.info("user {}", userDTO);

        httpServletResponse.sendRedirect("http://localhost:3000"); //redirect to page where user instance is fetched

        return userDTO;
    }

    @Operation(summary = "Get all users", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @GetMapping("/users")
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @Operation(summary = "Get user by email", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @GetMapping("/users/{email}")
    public UserDTO getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @Operation(summary = "Create user", description = "Does not need authorization header")
    @PostMapping("/users/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO,
                              boolean rememberMe,
                              HttpServletResponse httpServletResponse) {
        AccessTokenResponse accessTokenResponse = userService.createUser(userRegisterDTO);

        httpServletResponse.addCookie(CookieUtils.createAccessTokenCookie(accessTokenResponse.getToken()));

        if (rememberMe) {
            httpServletResponse.addCookie(CookieUtils.createRefreshTokenCookie(accessTokenResponse.getRefreshToken()));
        }

        return userService.getUserByEmail(userRegisterDTO.getEmail());
    }

    @Operation(summary = "Update user", description = "Needs authorization header")
    @PreAuthorize("hasRole('" + Role.USER + "')")
    @PutMapping("/users/update")
    public UserDTO updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.updateUser(userUpdateDTO);
    }

    @Operation(summary = "Get current user info", description = "Needs authorization header")
    @PreAuthorize("hasRole('" + Role.USER + "')")
    @GetMapping("/user/info")
    public UserDTO getUserInfo() {
        return userService.getCurrentUserInfoByUserId();
    }


    @Operation(summary = "Send otp to user", description = "Does not need authorization header")
    @PostMapping(value = "/users/send-otp", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void sendEmailForPasswordlessLogin(String email) throws InvalidKeyException {
        userService.sendEmailForPasswordlessLogin(email);
    }

    @Operation(summary = "Authenticate user with otp", description = "Does not need authorization header")
    @PostMapping("/users/authenticate-otp")
    public UserDTO authenticateUser(String email, int code, HttpServletResponse httpServletResponse) {
        AccessTokenResponse accessTokenResponse = userService.authenticateWithCode(email, code);

        httpServletResponse.addCookie(CookieUtils.createAccessTokenCookie(accessTokenResponse.getToken()));
        httpServletResponse.addCookie(CookieUtils.createRefreshTokenCookie(accessTokenResponse.getRefreshToken()));

        return userService.getUserByEmail(email);
    }

    @Operation(summary = "Get token for user", description = "Does not need authorization header")
    @PostMapping(value = "/users/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public UserDTO getToken(String email, String password, boolean rememberMe, HttpServletResponse httpServletResponse) {
        AccessTokenResponse accessTokenResponse = userService.getAccessToken(email, password);

        httpServletResponse.addCookie(CookieUtils.createAccessTokenCookie(accessTokenResponse.getToken()));

        if (rememberMe) {
            httpServletResponse.addCookie(CookieUtils.createRefreshTokenCookie(accessTokenResponse.getRefreshToken()));
        }

        return userService.getUserByEmail(email);
    }

    @Operation(summary = "Refresh token for user", description = "Does not need authorization header")
    @PostMapping(value = "/users/refresh-token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void refreshToken(String refreshToken, HttpServletResponse httpServletResponse) {
        AccessTokenResponse accessTokenResponse = userService.refreshToken(refreshToken);

        httpServletResponse.addCookie(CookieUtils.createAccessTokenCookie(accessTokenResponse.getToken()));
        httpServletResponse.addCookie(CookieUtils.createRefreshTokenCookie(accessTokenResponse.getRefreshToken()));
    }

    @Operation(summary = "Send verification email", description = "Needs authorization header")
    @PostMapping("/users/send-verification-email")
    @PreAuthorize("hasRole('" + Role.USER + "')")
    public void sendEmailForVerification() throws InvalidKeyException {
        userService.sendEmailForVerification();
    }

    @Operation(summary = "Verify email", description = "Needs authorization header")
    @PostMapping(value = "/users/verify-email", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @PreAuthorize("hasRole('" + Role.USER + "')")
    public void verifyEmail(int code) {
        userService.verifyEmail(code);
    }

    @Operation(summary = "Send email for reset password", description = "Does not need authorization header")
    @PostMapping(value = "/users/reset-password", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void sendEmailForResetPassword(String email) throws InvalidKeyException {
        userService.sendEmailForResetPassword(email);
    }

    @Operation(summary = "Reset password", description = "Does not need authorization header")
    @PostMapping(value = "/users/reset-password/verify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void resetPassword(String email, int code, String newPassword, String confirmPassword) {
        userService.resetPassword(email, code, newPassword, confirmPassword);
    }

    @Operation(summary = "Logout", description = "Needs authorization header")
    @PreAuthorize("hasRole('" + Role.USER + "')")
    @PostMapping("/user/logout")
    public void logout(HttpServletResponse httpServletResponse) {
        for (Cookie cookie : CookieUtils.clearCookies()) {
            httpServletResponse.addCookie(cookie);
        }

        userService.logout();
    }

    @Operation(summary = "Delete user by userId", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @DeleteMapping("/users/{userId}")
    public void deleteUserByUserId(@PathVariable String userId) {
        userService.deleteUserByUserId(userId);
    }
}