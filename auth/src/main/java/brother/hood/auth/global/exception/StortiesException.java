package brother.hood.auth.global.exception;

import lombok.Getter;
import brother.hood.auth.global.exception.error.ErrorCodes;

@Getter
public class StortiesException extends RuntimeException{

    private final ErrorCodes errorCodes;

    public StortiesException(ErrorCodes errorCodes) {
        super(errorCodes.getMessage());
        this.errorCodes = errorCodes;
    }
}
