package brother.hood.auth.global.auth;

import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticatedUserProvider {

    public Long getCurrentUserId() {
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
