package com.itihub.client.oauth;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Arrays;
import java.util.HashSet;

/**
 * AuthorizationCredentials
 * 授权凭证
 */
@Data
@Builder
public class AuthorizationCredentials {
    /**
     * Client Id
     */
    @NonNull
    private String clientId;

    /**
     * Client Secret
     */
    @NonNull
    private String clientSecret;

    /**
     * Refresh Token
     */
    private String refreshToken;

    /**
     * Authorization Server Endpoint
     */
    @NonNull
    private String endpoint;

    /**
     * Client Scopes
     */
    private ClientScopes scopes;

    public static class AuthorizationCredentialsBuilder {

        {
            scopes = new ClientScopes(new HashSet<>());
        }

        public AuthorizationCredentialsBuilder withScope(String scope) {
            return withScopes(scope);
        }

        public AuthorizationCredentialsBuilder withScopes(String... scopes) {
            if (scopes != null) {
                Arrays.stream(scopes) .forEach(this.scopes::addScope);
            }
            return this;
        }

    }

}
