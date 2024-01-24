package com.itihub.client;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationCredentials {

    @NonNull
    private String accessKeyId;

    @NonNull
    private String secretKey;
}
