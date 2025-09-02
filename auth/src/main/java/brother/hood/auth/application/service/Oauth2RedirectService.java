package brother.hood.auth.application.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class Oauth2RedirectService {
    public Map<String, String> execute(String provider) {
        String redirectUri = UriComponentsBuilder.fromUriString("http://localhost:8080/oauth2/authorization/" + provider)
                .build()
                .toString();

        return Map.of("url", redirectUri);
    }
}
