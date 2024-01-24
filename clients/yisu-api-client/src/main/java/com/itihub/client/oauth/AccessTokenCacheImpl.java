package com.itihub.client.oauth;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 访问令牌缓存实现
 * 默认基于内存实现缓存
 *
 * 以毫秒为时间单位，避免发出请求之前或发出请求时令牌过期
 */
public class AccessTokenCacheImpl implements AccessTokenCache {

    private final long EXPIRY_ADJUSTMENT = 60 * 1000;

    private static final long SECOND_TO_MILLIS = 1000;

    // 缓存存放容器
    private ConcurrentHashMap<Object, Object> accessTokenHashMap = new ConcurrentHashMap<Object, Object>();

    @Override
    public String get(Object key) {
        return null;
    }

    @Override
    public void put(Object key, String accessToken, long tokenTTLInSeconds) {

    }
}
