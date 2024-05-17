package com.store.webconfig;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private final Keycloak keycloak = new Keycloak();


    public Keycloak getKeycloak() {
        return keycloak;
    }

    public static class Keycloak {
        private String realm;
        private String baseUrl;
        private String clientId;
        private String clientSecret;

        private String googleClientId;
        private String googleClientSecret;
        private String scopes;

        public String getRealm() {
            return realm;
        }

        public void setRealm(String realm) {
            this.realm = realm;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getGoogleClientId() {
            return googleClientId;
        }

        public void setGoogleClientId(String googleClientId) {
            this.googleClientId = googleClientId;
        }

        public String getGoogleClientSecret() {
            return googleClientSecret;
        }

        public void setGoogleClientSecret(String googleClientSecret) {
            this.googleClientSecret = googleClientSecret;
        }

        public String getScopes() {
            return scopes;
        }

        public void setScopes(String scopes) {
            this.scopes = scopes;
        }
    }
}