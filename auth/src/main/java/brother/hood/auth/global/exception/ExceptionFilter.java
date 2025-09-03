package brother.hood.auth.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import brother.hood.auth.global.exception.error.ErrorCodes;
import brother.hood.auth.global.exception.error.ErrorResponse;

@RequiredArgsConstructor
@Slf4j
@Component
public class ExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AuthException authException) {
            errorToJson(authException.getErrorCodes(), response);
        }
        catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof AuthException) {
                errorToJson(((AuthException) cause).getErrorCodes(), response);
            } else {
                log.error(e.getMessage(), e);
                errorToJson(ErrorCodes.INTERNAL_SERVER_ERROR, response);
            }
        }
    }

    private void errorToJson(ErrorCodes errorProperty, HttpServletResponse response) throws IOException {
        response.setStatus(errorProperty.getStatus());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ErrorResponse.of(errorProperty)));
    }
}
