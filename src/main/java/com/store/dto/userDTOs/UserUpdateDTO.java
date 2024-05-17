package com.store.dto.userDTOs;

import com.store.annotation.validation.ValidAttributesMap;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UserUpdateDTO {
    private String email;
    private String firstName;
    private String lastName;
    @ValidAttributesMap(allowedKeys = {
            "deliveryAddressFirstName",
            "deliveryAddressLastName",
            "deliveryAddressCountry",
            "deliveryAddressZip",
            "deliveryAddressState",
            "deliveryAddressCity",
            "deliveryAddressPhone",
            "deliveryAddressCompany",
            "deliveryAddressLine1",
            "deliveryAddressLine2",
            "billingAddressFirstName",
            "billingAddressLastName",
            "billingAddressCountry",
            "billingAddressZip",
            "billingAddressState",
            "billingAddressCity",
            "billingAddressPhone",
            "billingAddressCompany",
            "billingAddressLine1",
            "billingAddressLine2",
            "subscription",
            "phoneNumber",
            "acceptanceOfTerms"})
    private Map<String, List<String>> attributes;
}