package com.store.dto.userDTOs;

import com.store.annotation.validation.ValidAttributesMap;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class UserRegisterDTO {
    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid email")
    private String email;

    @Size(min = 6, message = "Password must have 6 or more symbols")
    private String password;
    private String confirmPassword;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, message = "Field must have 2 or more characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 2, message = "Field must have 2 or more characters")
    private String lastName;

    @ValidAttributesMap(allowedKeys = {
            "deliveryAddressFirstName",
            "deliveryAddressLastName",
            "deliveryAddressCountry",
            "deliveryAddressZip",
            "deliveryAddressState",
            "deliveryAddressCity",
            "deliveryAddressPhone",
            "billingAddressFirstName",
            "billingAddressLastName",
            "billingAddressCountry",
            "billingAddressZip",
            "billingAddressState",
            "billingAddressCity",
            "billingAddressPhone",
            "subscription",
            "phoneNumber",
            "acceptanceOfTerms"})
    private Map<String, List<String>> attributes;
}