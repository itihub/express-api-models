package com.itihub.client.oauth;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义序列化反序列化
 */
public class ClientScopesSerializerDeserializer implements JsonDeserializer<ClientScopes>, JsonSerializer<ClientScopes> {

    /**
     * 重写序列化方法
     * @param src
     * @param typeOfSrc
     * @param context
     * @return
     */
    @Override
    public JsonElement serialize(ClientScopes src, Type typeOfSrc, JsonSerializationContext context) {
        // 将set集合序列化成字符串并用空格分隔
        return new JsonPrimitive(String.join(" ", src.getScopes()));
    }

    /**
     * 重写反序列化方法
     * @param jsonElement
     * @param type
     * @param jsonDeserializationContext
     * @return
     * @throws JsonParseException
     */
    @Override
    public ClientScopes deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        // 将字符串按空格分隔反序列化为Set集合
        Set<String> scopeSet = new HashSet<>(Arrays.asList(jsonObj.get("scope").getAsString().split(" ")));
        return new ClientScopes(scopeSet);

    }
}
