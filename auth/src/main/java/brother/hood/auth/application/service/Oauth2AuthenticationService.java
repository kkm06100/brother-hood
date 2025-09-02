package brother.hood.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import brother.hood.auth.application.service.dto.response.AllTokenResponse;
import brother.hood.auth.application.service.util.JwtUtil;
import brother.hood.auth.persistence.User;
import brother.hood.auth.persistence.repository.UserJpaRepository;
import brother.hood.auth.persistence.type.AuthProvider;
import brother.hood.auth.persistence.type.Role;

@Service
@RequiredArgsConstructor
public class Oauth2AuthenticationService {

    private final JwtUtil jwtUtil;

    private final UserJpaRepository userJpaRepository;

    public AllTokenResponse execute(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String providerId = oAuth2User.getAttribute("sub");

        User user = userJpaRepository.findByEmail(email)
                .orElseGet(() -> userJpaRepository.save(User.builder()
                        .role(Role.USER)
                        .email(email)
                        .oauthProviderId(providerId)
                        .authProvider(AuthProvider.GOOGLE)
                        .build()));

        return jwtUtil.createAllToken(user.getId(), user.getRole());
    }
}
