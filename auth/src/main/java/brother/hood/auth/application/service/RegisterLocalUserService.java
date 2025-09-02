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
import brother.hood.auth.persistence.type.AuthProvider;
import brother.hood.auth.persistence.type.Role;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterLocalUserService {

    private final UserJpaRepository userJpaRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AllTokenResponse execute(AuthUserRequest request) {
        if(userJpaRepository.existsByEmail(request.getEmail())) {
            throw ErrorCodes.EMAIL_ALREADY_EXIST.throwException();
        }

        User user = userJpaRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email(request.getEmail())
                .role(Role.USER)
                .password(passwordEncoder.encode(request.getPassword()))
                .build());

        return jwtUtil.createAllToken(user.getId(), user.getRole());
    }
}
