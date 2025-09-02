package brother.hood.auth.application.service.util;

import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import brother.hood.auth.application.service.dto.response.AccessTokenResponse;
import brother.hood.auth.application.service.dto.response.AllTokenResponse;
import brother.hood.auth.global.auth.JwtProvider;
import brother.hood.auth.persistence.type.Role;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProvider jwtProvider;

    static private final String TOKEN = "token";

    static private final String EXPIRES_AT = "expiresAt";

    public AllTokenResponse createAllToken(Long id, Role role) {

        Map<String, Object> refreshToken = jwtProvider.createRefreshToken(id);
        Map<String, Object> accessToken = jwtProvider.createAccessToken(id, role);

        return AllTokenResponse.builder()
                .accessToken((String) accessToken.get(TOKEN))
                .accessTokenExpiresAt((Date) accessToken.get(EXPIRES_AT))
                .refreshToken((String) refreshToken.get(TOKEN))
                .refreshTokenExpiresAt((Date) refreshToken.get(EXPIRES_AT))
                .build();
    }

    public AccessTokenResponse createAccessToken(Long id, Role role) {
        Map<String, Object> accessToken = jwtProvider.createAccessToken(id, role);

        return AccessTokenResponse.builder()
                .accessToken((String) accessToken.get(TOKEN))
                .accessTokenExpiresAt((Date) accessToken.get(EXPIRES_AT))
                .build();
    }
}
