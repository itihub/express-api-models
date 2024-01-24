package com.itihub.client.oauth;

import com.squareup.okhttp.Request;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Authorization Signer
 * 授权签名
 */
public class AuthorizationSigner {
    private static final String SIGNED_ACCESS_TOKEN_HEADER_NAME = "x-amz-access-token";

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private OAuthClient authClient;

    private AccessTokenRequestMeta accessTokenRequestMeta;


     /**
     * 令牌交换
     * @param authorizationCredentials Authorization Credentials for token exchange
     */
    public AuthorizationSigner(AuthorizationCredentials authorizationCredentials) {

        authClient = new OAuthClient(authorizationCredentials.getEndpoint());

        buildAccessTokenRequestMeta(authorizationCredentials);

    }

    /**
    *
    * Overloaded Constructor @param lwaAuthorizationCredentials LWA Authorization Credentials for token exchange
    * and LWAAccessTokenCache
    */
    public AuthorizationSigner(AuthorizationCredentials authorizationCredentials,
                               AccessTokenCache accessTokenCache) {

       authClient = new OAuthClient(authorizationCredentials.getEndpoint());
       authClient.setLWAAccessTokenCache(accessTokenCache);

       buildAccessTokenRequestMeta(authorizationCredentials);

   }

    /**
     *  Signs a Request with an LWA Access Token
     * @param originalRequest Request to sign (treated as immutable)
     * @return Copy of originalRequest with LWA signature
     */
    public Request sign(Request originalRequest) {
        String accessToken = authClient.getAccessToken(accessTokenRequestMeta);

        return originalRequest.newBuilder()
                .addHeader(SIGNED_ACCESS_TOKEN_HEADER_NAME, accessToken)
                .build();
    }

    /**
     * 构建请求元数据
     * @param authorizationCredentials
     */
    private void buildAccessTokenRequestMeta(AuthorizationCredentials authorizationCredentials) {
        String tokenRequestGrantType;
        if (!authorizationCredentials.getScopes().isEmpty()) {
            tokenRequestGrantType = "client_credentials";
        } else {
            tokenRequestGrantType = "refresh_token";
        }

        accessTokenRequestMeta = AccessTokenRequestMeta.builder()
                .clientId(authorizationCredentials.getClientId())
                .clientSecret(authorizationCredentials.getClientSecret())
                .refreshToken(authorizationCredentials.getRefreshToken())
                .grantType(tokenRequestGrantType).scopes(authorizationCredentials.getScopes())
                .build();
    }
}
