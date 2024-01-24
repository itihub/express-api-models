package com.itihub.client.oauth;

import com.google.gson.annotations.JsonAdapter;

import java.util.Set;

/**
 *
 */
@JsonAdapter(ClientScopesSerializerDeserializer.class)
public class ClientScopes {

    private final Set<String> scopes;

    public ClientScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    protected void addScope(String scope) {
        scopes.add(scope);

    }

    protected boolean isEmpty() {
        return scopes.isEmpty();
    }
}
