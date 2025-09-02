package brother.hood.auth.presentation;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import brother.hood.auth.application.service.LoginService;
import brother.hood.auth.application.service.Oauth2RedirectService;
import brother.hood.auth.application.service.RegisterLocalUserService;
import brother.hood.auth.application.service.ReissueService;
import brother.hood.auth.application.service.dto.request.AuthUserRequest;
import brother.hood.auth.application.service.dto.response.AccessTokenResponse;
import brother.hood.auth.application.service.dto.response.AllTokenResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final LoginService loginService;
    private final RegisterLocalUserService registerLocalUserService;
    private final ReissueService reissueService;
    private final Oauth2RedirectService redirectService;

    @PostMapping("/login")
    public AllTokenResponse login(@RequestBody AuthUserRequest request) {
        return loginService.execute(request);
    }

    @PostMapping("/register")
    public AllTokenResponse register(@RequestBody AuthUserRequest request) {
        return registerLocalUserService.execute(request);
    }

    @PostMapping("/reissue")
    public AccessTokenResponse reissue(@RequestHeader("refresh-token") String refreshToken) {
        return reissueService.execute(refreshToken);
    }

    @PostMapping("/oauth/{provider}")
    public Map<String, String> redirectToProvider(@PathVariable String provider) {
        return redirectService.execute(provider);
    }
}
