package com.itihub.client;

import com.squareup.okhttp.Request;

/**
 * 亿速 Signature Version 1 Signer
 */
public class YisuSigV1Signer {


    private Credentials credentials;

    /**
     * 接口版本，默认”V1.0”
     */
    private String version = "V1.0";

    private YisuSigner yisuSigner;

    public YisuSigV1Signer(AuthenticationCredentials authenticationCredentials) {
        this.credentials = new BasicCredentials(authenticationCredentials.getAccessKeyId(), authenticationCredentials.getSecretKey());
        this.yisuSigner = new YisuSigner();
    }

    public YisuSigV1Signer(AuthenticationCredentials authenticationCredentials, String version) {
        this.credentials = new BasicCredentials(authenticationCredentials.getAccessKeyId(), authenticationCredentials.getSecretKey());
        this.version = version;
        this.yisuSigner = new YisuSigner(version);
    }

    /**
     * 签署请求
     * @param originalRequest
     * @return
     */
    public Request sign(Request originalRequest) {
        SignableRequest<Request> signableRequest = new SignableRequestImpl(originalRequest);
        yisuSigner.sign(signableRequest, credentials);
        return (Request) signableRequest.getOriginalRequestObject();
    }

}
