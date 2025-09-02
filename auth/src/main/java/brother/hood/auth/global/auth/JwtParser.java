package brother.hood.auth.global.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import brother.hood.auth.global.auth.token.Token;
import brother.hood.auth.global.exception.error.ErrorCodes;

@Slf4j
@Component
public class JwtParser {

    private final io.jsonwebtoken.JwtParser jwtParser;

    public JwtParser(JwtProperties jwtProperties) {
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.SECRET.getBytes());
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    /**
     * 엑세스 토큰에서 인증 정보 추출 (ROLE_ 접두사 포함)
     */
    public Authentication getAuthentication(String accessToken) {
        Long id = getId(accessToken);
        String role = getRoleByAccessToken(accessToken);
        return new UsernamePasswordAuthenticationToken(
            id,
            null,
            List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    public String getRoleByAccessToken(String accessToken) {
        return getClaims(accessToken).get(JwtProperties.ROLE, String.class);
    }

    public Long getId(String accessToken) {
        try {
            return Long.valueOf(getClaims(accessToken).getSubject());
        } catch (Exception e) {
            throw ErrorCodes.INVALID_TOKEN.throwException();
        }
    }

    /**
     * 엑세스 토큰 유효성 검사 (만료 및 토큰 타입)
     * 만료 시 TokenExpiredException 발생
     */
    public boolean validateAccessToken(String accessToken) {
        try {
            Claims claims = getClaims(accessToken);

            if (claims.getExpiration().before(new Date())) {
                throw ErrorCodes.TOKEN_EXPIRED.throwException();
            }
            return getRoleByAccessToken(accessToken).equals(Token.ACCESS_TOKEN.name());

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid access token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 리프레시 토큰 유효성 검사 (만료 및 토큰 타입)
     * 만료 시 TokenExpiredException 발생
     */
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Claims claims = getClaims(refreshToken);

            if (claims.getExpiration().before(new Date())) {
                throw ErrorCodes.TOKEN_EXPIRED.throwException();
            }

            return JwtProperties.REFRESH_TOKEN.equals(
                claims.get(JwtProperties.TOKEN_TYPE, String.class)
            );
        } catch (JwtException e) {
            log.warn("Invalid refresh token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰에서 Claims 추출 (서명 검증 포함)
     */
    private Claims getClaims(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw ErrorCodes.TOKEN_EXPIRED.throwException();
        } catch (Exception e) {
            throw ErrorCodes.INVALID_TOKEN.throwException();
        }

    }
}
