package com.itihub.client;

import java.io.InputStream;
import java.lang.reflect.Executable;

public interface SignableRequest<T> extends ImmutableRequest<T> {

    void addHeader(String var1, String var2);

    void addParameter(String var1, String var2);

    Exception setContent(InputStream var1);

}
