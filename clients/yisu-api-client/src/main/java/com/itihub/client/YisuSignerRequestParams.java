package com.itihub.client;

import java.time.Instant;
import java.util.Date;

public final class YisuSignerRequestParams {

    private final SignableRequest<?> request;

    private final String version;
    private final long signingDateTimeMilli;
    /**
     * 签名算法
     */
    private final String signingAlgorithm;

    public YisuSignerRequestParams(SignableRequest<?> request, String version, Date signingDateOverride) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        this.request = request;
        this.version = version == null ? request.getParameters().get("version").stream().findFirst().orElse("V1.0") : version;
        this.signingDateTimeMilli = signingDateOverride != null ? signingDateOverride.getTime() : this.getSigningDate(request);
        this.signingAlgorithm = null;
    }

    /**
     * 从请求种获取签名日期
     * @param request
     * @return
     */
    private final long getSigningDate(SignableRequest<?> request) {
        return Instant.now().toEpochMilli() - request.getTimeOffset() * 1000L;
    }


    public String getFormattedSigningDateTime() {
        return String.valueOf(signingDateTimeMilli);
    }

    public String getSigningAlgorithm() {
        return signingAlgorithm;
    }

    public String getVersion() {
        return version;
    }
}
