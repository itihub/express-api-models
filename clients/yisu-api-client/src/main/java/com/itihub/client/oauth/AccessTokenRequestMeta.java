package com.itihub.client.oauth;


import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 获取AccessToken请求元数据
 */
@Getter
@Setter
@Builder
public class AccessTokenRequestMeta {

    @SerializedName("grant_type")
    private String grantType;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("client_id")
    private String clientId;

    @SerializedName("client_secret")
    private String clientSecret;

    @SerializedName("scope")
    private ClientScopes scopes;
}
