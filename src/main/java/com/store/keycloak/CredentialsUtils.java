package com.store.keycloak;

import org.keycloak.representations.idm.CredentialRepresentation;

public final class CredentialsUtils {
    public static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    public static CredentialRepresentation createOtp(int otp) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(String.valueOf(otp));
        return passwordCredentials;
    }
}