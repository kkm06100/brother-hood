package brother.hood.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import brother.hood.auth.application.service.dto.request.AuthUserRequest;
import brother.hood.auth.application.service.dto.response.AllTokenResponse;
import brother.hood.auth.application.service.util.JwtUtil;
import brother.hood.auth.global.exception.error.ErrorCodes;
import brother.hood.auth.persistence.User;
import brother.hood.auth.persistence.repository.UserJpaRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {

    private final PasswordEncoder passwordEncoder;

    private final UserJpaRepository userJpaRepository;

    private final JwtUtil jwtUtil;

    public AllTokenResponse execute(AuthUserRequest authUserRequest) {

        User user = userJpaRepository.findByEmail(authUserRequest.getEmail())
                .orElseThrow(ErrorCodes.EMAIL_ALREADY_EXIST::throwException);

        if(!passwordEncoder.matches(authUserRequest.getPassword(), user.getPassword())) {
            throw ErrorCodes.PASSWORD_MISMATCH.throwException();
        }

        return jwtUtil.createAllToken(user.getId(), user.getRole());
    }
}
