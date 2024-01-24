package com.itihub.client;

import org.apache.commons.lang3.StringUtils;

public enum HttpMethodName {

    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    PATCH,
    OPTIONS;

    private HttpMethodName() {
    }

    public static HttpMethodName fromValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        } else {
            String upperCaseValue = StringUtils.upperCase(value);
            HttpMethodName[] var2 = values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                HttpMethodName httpMethodName = var2[var4];
                if (httpMethodName.name().equals(upperCaseValue)) {
                    return httpMethodName;
                }
            }

            throw new IllegalArgumentException("Unsupported HTTP method name " + value);
        }
    }
}
