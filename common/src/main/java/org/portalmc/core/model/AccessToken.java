package org.portalmc.core.model;

public class AccessToken {
    public final String resource;
    public final String tokenType;
    public final String accessToken;
    public final int expiresIn;

    public AccessToken(String resource, String tokenType, String accessToken, int expiresIn) {
        this.resource = resource;
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}
