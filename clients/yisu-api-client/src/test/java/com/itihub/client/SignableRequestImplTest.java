package com.itihub.client;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.*;


public class SignableRequestImplTest {
    private Request testRequest;
    private SignableRequestImpl underTest;

    @BeforeEach
    public void init() {
        // 构建请求
        testRequest = new Request.Builder()
                .url("http://www.amazon.com/request/library?test=true&sky=blue&right=右")
                .get()
                .build();

        underTest = new SignableRequestImpl(testRequest);
    }

    @Test
    public void getHttpMethod() {
        // 验证请求方法
        Assertions.assertEquals(HttpMethodName.GET, underTest.getHttpMethod());

        underTest = new SignableRequestImpl(new Request.Builder()
                .url("https://www.amazon.com")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{\"foo\": \"bar\"}"))
                .build());

        Assertions.assertEquals(HttpMethodName.POST, underTest.getHttpMethod());
    }

    @Test
    public void getOriginalRequestObject() {
        Request actualRequest = (Request) underTest.getOriginalRequestObject();

        Assertions.assertNotSame(testRequest, actualRequest);
        Assertions.assertEquals(testRequest.method(), actualRequest.method());
        Assertions.assertEquals(testRequest.url(), actualRequest.url());
        Assertions.assertEquals(testRequest.headers().toMultimap(), actualRequest.headers().toMultimap());
        Assertions.assertEquals(testRequest.body(), actualRequest.body());
    }

    @Test
    public void getReadLimitInfo() {
        Assertions.assertNull(underTest.getReadLimitInfo());
    }

    @Test
    public void getResourcePath() {
        Assertions.assertEquals("/request/library", underTest.getResourcePath());
    }

    @Test
    public void getResourcePathWithPoundChar() {
        testRequest = new Request.Builder()
                .url("http://www.amazon.com/request/%23library")
                .get()
                .build();
        underTest = new SignableRequestImpl(testRequest);

        Assertions.assertEquals("/request/%23library", underTest.getResourcePath());
    }

    @Test
    public void noTimeOffset() {
        Assertions.assertEquals(0, underTest.getTimeOffset());
    }

    @Test
    public void getEndpoint() {
        Assertions.assertEquals(URI.create("http://www.amazon.com"), underTest.getEndpoint());
    }

    @Test
    public void headers() {
        Map<String, String> expectedHeaders = new HashMap<>();

        Assertions.assertTrue(underTest.getHeaders().isEmpty());

        underTest.addHeader("foo", "bar");
        expectedHeaders.put("foo", "bar");
        Assertions.assertEquals(expectedHeaders, underTest.getHeaders());

        underTest.addHeader("ban", "bop");
        expectedHeaders.put("ban", "bop");
        Assertions.assertEquals(expectedHeaders, underTest.getHeaders());
    }

    @Test
    public void getParameters() {
        Map<String, List<String>> expectedParamters = new HashMap<>();
        expectedParamters.put("test", Collections.singletonList("true"));
        expectedParamters.put("sky", Collections.singletonList("blue"));
        expectedParamters.put("right", Collections.singletonList("右"));

        Assertions.assertEquals(expectedParamters, underTest.getParameters());
    }

    @Test
    public void getContent() {
        String expectedContent = "{\"foo\":\"bar\"}";
        StringBuilder actualContent = new StringBuilder();

        underTest = new SignableRequestImpl(new Request.Builder()
                .url("https://www.amazon.com")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), expectedContent))
                .build());

        try (Scanner scanner = new Scanner(underTest.getContent())) {
            while (scanner.hasNext()) {
                actualContent.append(scanner.next());
            }
        }

        Assertions.assertEquals(expectedContent, actualContent.toString());
    }

    @Test
    public void getUnwrappedContent() {
        String expectedContent = "{\"ban\":\"bop\"}";
        StringBuilder actualContent = new StringBuilder();

        underTest = new SignableRequestImpl(new Request.Builder()
                .url("https://www.amazon.com")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), expectedContent))
                .build());

        try (Scanner scanner = new Scanner(underTest.getContentUnwrapped())) {
            while (scanner.hasNext()) {
                actualContent.append(scanner.next());
            }
        }

        Assertions.assertEquals(expectedContent, actualContent.toString());
    }

    @Test
    public void setContentNotSupported() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            underTest.setContent(new ByteArrayInputStream("abc".getBytes()));
        });
    }

    @Test
    public void addParameter() {
        underTest.addParameter("left", "左");

        HttpUrl actualHttpUrl = ((Request) underTest.getOriginalRequestObject())
                .httpUrl();

        Assertions.assertEquals(Collections.singletonList("true"), actualHttpUrl.queryParameterValues("test"));
        Assertions.assertEquals(Collections.singletonList("blue"), actualHttpUrl.queryParameterValues("sky"));
        Assertions.assertEquals(Collections.singletonList("右"), actualHttpUrl.queryParameterValues("right"));
        Assertions.assertEquals(Collections.singletonList("左"), actualHttpUrl.queryParameterValues("left"));
    }

    @Test
    public void gracefulBlankParametersParse() {
        testRequest = new Request.Builder()
                .url("http://www.amazon.com/request/library?  ")
                .get()
                .build();

        underTest = new SignableRequestImpl(testRequest);

        Assertions.assertTrue(underTest.getParameters().isEmpty());
    }

    @Test
    public void gracefulIncompleteParameterPairsParse() {
        testRequest = new Request.Builder()
                .url("http://www.amazon.com/request/library?isSigned& =false")
                .get()
                .build();

        Map<String, List<String>> expected = new HashMap<>();
        expected.put("isSigned", Collections.singletonList(null));
        expected.put(" ", Collections.singletonList("false"));

        underTest = new SignableRequestImpl(testRequest);

        Assertions.assertEquals(expected, underTest.getParameters());
    }

    @Test
    public void getHeadersIncludesContentTypeFromRequestBody() {
        String expected = "application/json; charset=utf-8";
        RequestBody requestBody = RequestBody.create(MediaType.parse(expected),
                "{\"foo\":\"bar\"}");

        testRequest = new Request.Builder()
                .url("http://www.amazon.com")
                .post(requestBody)
                .header("Content-Type", "THIS SHOULD BE OVERRIDDEN WITH REQUEST BODY CONTENT TYPE")
                .build();

        underTest = new SignableRequestImpl(testRequest);

        Assertions.assertEquals(expected, underTest.getHeaders().get("Content-Type"));
    }

    @Test
    public void missingRequestBodyDoesNotOverwriteExistingContentTypeHeader() {
        String expected = "testContentType";

        testRequest = new Request.Builder()
                .url("http://www.amazon.com")
                .get()
                .header("Content-Type", expected)
                .build();

        underTest = new SignableRequestImpl(testRequest);

        Assertions.assertEquals(expected, underTest.getHeaders().get("Content-Type"));
    }

    @Test
    public void missingRequestBodyContentTypeDoesNotOverwriteExistingContentTypeHeader() {
        String expected = "testContentType";

        testRequest = new Request.Builder()
                .url("http://www.amazon.com")
                .post(RequestBody.create(null, "foo"))
                .header("Content-Type", expected)
                .build();

        underTest = new SignableRequestImpl(testRequest);

        Assertions.assertEquals(expected, underTest.getHeaders().get("Content-Type"));
    }
}
