package org.example.post.global.authentication;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    String secret,
    long accessTokenExpiration,
    long refreshTokenExpiration
) {
}
