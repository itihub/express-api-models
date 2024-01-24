package com.itihub.client.oauth;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 自定义序列化反序列化测试
 */
public class ClientScopesSerializerDeserializerTest {

    private static final Set<String> scopesTestSellerless = new HashSet<String>(
            Arrays.asList("testScope::notifications", "testScope::migration")
    );

    private static final String SELLER_TYPE_SELLER = "seller";
    private static final String SELLER_TYPE_SELLERLESS = "sellerless";

    private Gson gson;

    @BeforeEach
    public void setup() {
        gson = new Gson();
    }

    public static Stream<Arguments> scopeSerialization() {

        return Stream.of(
                Arguments.of(SELLER_TYPE_SELLER, null),
                Arguments.of(SELLER_TYPE_SELLERLESS, new ClientScopes(scopesTestSellerless))
        );
    }

    public static Stream<Arguments> scopeDeserialization() {

        return Stream.of(
                Arguments.of(SELLER_TYPE_SELLER, null),
                Arguments.of(SELLER_TYPE_SELLERLESS, "{\"scope\":\"testScope::migration testScope::notifications\"}")
        );
    }

    /**
     * 序列化测试
     *
     * @param sellerType
     * @param testScope
     * @ParameterizedTest 注解定义为参数化测试
     * @ValueSource 注解定义参数值(输入)
     * @MethodSource 注解定义参数值获取方法
     */
    @ParameterizedTest
    @MethodSource("scopeSerialization")
    public void testSerializeScope(String sellerType, ClientScopes testScope) {

        String scopeJSON = gson.toJson(testScope);

        if (sellerType.equals(SELLER_TYPE_SELLER)) {
            Assertions.assertEquals("null", scopeJSON);
        } else if (sellerType.equals(SELLER_TYPE_SELLERLESS)) {
            Assertions.assertTrue(!scopeJSON.isEmpty());
        }
    }

    /**
     * 反序列化测试
     *
     * @param sellerType
     * @param serializedValue
     */
    @ParameterizedTest
    @MethodSource("scopeDeserialization")
    public void testDeserializeScope(String sellerType, String serializedValue) {

        ClientScopes deserializedValue = gson.fromJson(serializedValue, ClientScopes.class);
        if (sellerType.equals(SELLER_TYPE_SELLER)) {
            Assertions.assertNull(deserializedValue);
        } else if (sellerType.equals(SELLER_TYPE_SELLERLESS)) {
            Assertions.assertEquals(deserializedValue.getScopes(), scopesTestSellerless);
        }
    }

}
