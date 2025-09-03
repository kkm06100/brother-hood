package brother.hood.auth.global.exception;

import lombok.Getter;
import brother.hood.auth.global.exception.error.ErrorCodes;

@Getter
public class AuthException extends RuntimeException{

    private final ErrorCodes errorCodes;

    public AuthException(ErrorCodes errorCodes) {
        super(errorCodes.getMessage());
        this.errorCodes = errorCodes;
    }
}
