package brother.hood.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import brother.hood.auth.application.service.dto.response.AccessTokenResponse;
import brother.hood.auth.application.service.util.JwtUtil;
import brother.hood.auth.global.auth.JwtParser;
import brother.hood.auth.global.exception.error.ErrorCodes;
import brother.hood.auth.persistence.User;
import brother.hood.auth.persistence.repository.UserJpaRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ReissueService {

    private final UserJpaRepository userJpaRepository;

    private final JwtUtil jwtUtil;

    private final JwtParser jwtParser;

    public AccessTokenResponse execute(String refreshToken){
        String token = refreshToken.replace("Bearer ", "");

        if(!jwtParser.validateRefreshToken(token)) {
            throw ErrorCodes.INVALID_TOKEN.throwException();
        }

        User user = userJpaRepository.findById(jwtParser.getId(token))
                .orElseThrow(ErrorCodes.USER_NOT_FOUND::throwException);

        return jwtUtil.createAccessToken(user.getId(), user.getRole());
    }
}
