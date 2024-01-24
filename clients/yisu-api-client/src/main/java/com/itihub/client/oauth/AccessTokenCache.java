package com.itihub.client.oauth;

/**
 * 访问令牌缓存
 */
public interface AccessTokenCache {

    String get(Object key);

    void put(Object key, String accessToken, long tokenTTLInSeconds);
}
