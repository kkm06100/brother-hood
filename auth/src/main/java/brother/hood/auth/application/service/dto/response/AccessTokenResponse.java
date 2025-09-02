package brother.hood.auth.application.service.dto.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccessTokenResponse {

    private String accessToken;

    private Date accessTokenExpiresAt;
}
