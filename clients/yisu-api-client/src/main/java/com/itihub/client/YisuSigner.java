package com.itihub.client;

import lombok.extern.java.Log;
import org.apache.commons.codec.digest.Md5Crypt;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Log
public class YisuSigner {

    private String version;

    private long signingDateTimeMilli;

    protected Date overriddenDate;

    /**
     * 签名算法
     */
    private String signingAlgorithm;


    public YisuSigner() {
    }

    public YisuSigner(String version) {
        this.version = version;
    }

    public YisuSigner(String version, long signingDateTimeMilli, String signingAlgorithm) {
        this.version = version;
        this.signingDateTimeMilli = signingDateTimeMilli;
        this.signingAlgorithm = signingAlgorithm;
    }

    public void sign(SignableRequest<?> request, Credentials credentials) {
        YisuSignerRequestParams signerParams = new YisuSignerRequestParams(request, version, overriddenDate);
        this.addHostHeader(request);
        request.addHeader("appid", credentials.getSecretKey());
        request.addHeader("version", signerParams.getVersion());
        request.addHeader("timestamp", signerParams.getFormattedSigningDateTime());

        String stringToSign = this.createStringToSign(signerParams, credentials);
        String signature = this.computeSignature(stringToSign);
        request.addHeader("sign", signature);
        this.processRequestPayload(request, signature, signerParams);
    }

    protected void processRequestPayload(SignableRequest<?> request, String signature, YisuSignerRequestParams signerRequestParams) {
        return;
    }

    protected void addHostHeader(SignableRequest<?> request) {
        // AWS4 requires that we sign the Host header so we
        // have to have it in the request by the time we sign.

        final URI endpoint = request.getEndpoint();
        final StringBuilder hostHeaderBuilder = new StringBuilder(
                endpoint.getHost());
//        if (SdkHttpUtils.isUsingNonDefaultPort(endpoint)) {
//            hostHeaderBuilder.append(":").append(endpoint.getPort());
//        }

        request.addHeader("HOST", hostHeaderBuilder.toString());
    }

    protected String createStringToSign(YisuSignerRequestParams signerParams, Credentials credentials) {

        final StringBuilder stringToSignBuilder = new StringBuilder(signerParams.getSigningAlgorithm());

        stringToSignBuilder.append(credentials.getAccessKeyId())
                .append(signerParams.getVersion())
                .append(signerParams.getFormattedSigningDateTime())
                .append(credentials.getSecretKey());
        final String stringToSign = stringToSignBuilder.toString();
//        if (log.isDebugEnabled())
//            log.debug("AWS4 String to Sign: '\"" + stringToSign + "\"");
//        }
        return stringToSign;
    }

    protected final String computeSignature(String stringToSign) {
        return Md5Crypt.md5Crypt(stringToSign.getBytes(StandardCharsets.UTF_8));
    }

}
