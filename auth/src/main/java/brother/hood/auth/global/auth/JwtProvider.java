package brother.hood.auth.global.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import brother.hood.auth.global.auth.token.Token;
import brother.hood.auth.persistence.type.Role;

/**
 * 토큰 생성 밑 파싱
 */

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final RedisTemplate<String, String> redisTemplate;

    private final JwtProperties jwtProperties;

    /**
     * 토큰 생성 시 Role은 String 형태로 저장됨
     * @param id 아이디
=     * @return 토큰
     */
    public Map<String, Object> createAccessToken(Long id, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtProperties.ROLE, String.valueOf(role));
        claims.put(JwtProperties.TOKEN_TYPE, String.valueOf(Token.ACCESS_TOKEN));

        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.SECRET.getBytes());

        long validity = jwtProperties.ACCESS_TOKEN_EXPIRES_AT;
        Date now = new Date();
        Date exp = new Date(now.getTime() + validity);

        String token = Jwts.builder()
            .setSubject(String.valueOf(id))
            .addClaims(claims)
            .setIssuedAt(now)
            .setExpiration(exp)
            .signWith(key)
            .compact();

        Map<String, Object> response = new HashMap<>();
        response.put(JwtProperties.TOKEN, token);
        response.put(JwtProperties.EXPIRES_IN, validity / 1000);
        response.put(JwtProperties.EXPIRES_AT, exp);

        return response;
    }

    private Map<String, Object> buildResponse(String token, long expireSeconds) {
        Map<String, Object> response = new HashMap<>();
        response.put(JwtProperties.TOKEN, token);
        response.put(JwtProperties.EXPIRES_IN, expireSeconds);
        response.put(JwtProperties.EXPIRES_AT, new Date(System.currentTimeMillis() + expireSeconds * 1000));
        return response;
    }

    public Map<String, Object> createRefreshToken(Long id) {
        String redisKey = "RT:" + id;

        String existingToken = redisTemplate.opsForValue().get(redisKey);
        Long expireSeconds = redisTemplate.getExpire(redisKey);
        if (!(expireSeconds == null || expireSeconds <= 0)) {
            return buildResponse(existingToken, expireSeconds);
        }

        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.SECRET.getBytes());
        Date now = new Date();
        long validity = 14 * 24 * 60 * 60 * 1000L;
        Date exp = new Date(now.getTime() + validity);

        String refreshToken = Jwts.builder()
            .setSubject(String.valueOf(id))
            .claim(JwtProperties.TOKEN_TYPE, String.valueOf(Token.REFRESH_TOKEN))
            .setIssuedAt(now)
            .setExpiration(exp)
            .signWith(key)
            .compact();

        redisTemplate.opsForValue().set(redisKey, refreshToken, Duration.ofMillis(validity));

        return buildResponse(refreshToken, validity / 1000);
    }

}
