package org.example.post.global.exception;

import lombok.Getter;
import org.example.post.global.exception.error.ErrorCodes;

@Getter
public class StortiesException extends RuntimeException{

    private final ErrorCodes errorCodes;

    public StortiesException(ErrorCodes errorCodes) {
        super(errorCodes.getMessage());
        this.errorCodes = errorCodes;
    }
}
