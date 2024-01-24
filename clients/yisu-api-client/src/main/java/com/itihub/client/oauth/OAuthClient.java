package com.itihub.client.oauth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

/**
 * OAuth认证客户端
 */
public class OAuthClient {

    // 常量定义
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String ACCESS_TOKEN_EXPIRES_IN = "expires_in";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    /* 请求端点 */
    @Getter
    private String endpoint;

    /* HTTP客户端 */
    @Setter(AccessLevel.PACKAGE)
    private OkHttpClient okHttpClient;

    /* 缓存 */
    private AccessTokenCache accessTokenCache;

    OAuthClient(String endpoint) {
        okHttpClient = new OkHttpClient();
        this.endpoint = endpoint;
    }

    public void setLWAAccessTokenCache(AccessTokenCache tokenCache) {
        this.accessTokenCache = tokenCache;
    }

    /**
     * 获取AccessToken
     * @param accessTokenRequestMeta
     * @return
     */
    String getAccessToken(AccessTokenRequestMeta accessTokenRequestMeta) {
        if (accessTokenCache != null) {
            // 从缓存获取AccessToken
            return getAccessTokenFromCache(accessTokenRequestMeta);
        } else {
            // 请求获取AccessToken
            return getAccessTokenFromEndpoint(accessTokenRequestMeta);
        }
    }

    /**
     * 从缓存获取AccessToken
     * @param accessTokenRequestMeta
     * @return
     */
    String getAccessTokenFromCache(AccessTokenRequestMeta accessTokenRequestMeta) {
        String accessTokenCacheData = (String) accessTokenCache.get(accessTokenRequestMeta);
        if (accessTokenCacheData != null) {
            return accessTokenCacheData;
        } else {
            // 缓存不存在则进行远程请求获取
            return getAccessTokenFromEndpoint(accessTokenRequestMeta);
        }
    }

    String getAccessTokenFromEndpoint(AccessTokenRequestMeta accessTokenRequestMeta) {
        // 序列化请求body
        RequestBody requestBody = RequestBody.create(JSON_MEDIA_TYPE, new Gson().toJson(accessTokenRequestMeta));
        // 构建请求
        Request accessTokenRequest = new Request.Builder().url(endpoint).post(requestBody).build();

        String accessToken;
        try {
            // 进行远程请求
            Response response = okHttpClient.newCall(accessTokenRequest).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unsuccessful LWA token exchange");
            }

            JsonObject responseJson = new JsonParser().parse(response.body().string()).getAsJsonObject();

            accessToken = responseJson.get(ACCESS_TOKEN_KEY).getAsString();
            if (accessTokenCache != null) {
                // 将AccessToken进行缓存
                long timeToTokenExpiry = responseJson.get(ACCESS_TOKEN_EXPIRES_IN).getAsLong();
                accessTokenCache.put(accessTokenRequestMeta, accessToken, timeToTokenExpiry);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting OAuth Access Token", e);
        }
        return accessToken;
    }

}
