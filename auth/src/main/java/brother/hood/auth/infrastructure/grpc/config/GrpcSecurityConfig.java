package brother.hood.auth.infrastructure.grpc.config;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import java.util.Collections;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import net.devh.boot.grpc.server.security.authentication.AnonymousAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@GrpcGlobalServerInterceptor
public class GrpcSecurityConfig implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        ServerCallHandler<ReqT, RespT> next) {

        // 인증 로직 (예: 헤더에서 토큰 검증)
        String token = headers.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER));

        if (isValidToken(token)) {
            return next.startCall(call, headers);
        } else {
            call.close(Status.UNAUTHENTICATED.withDescription("Invalid token"), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }
    }

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        return new GrpcAuthenticationReader() {
            @Override
            public Authentication readAuthentication(ServerCall<?, ?> call, Metadata headers) {
                return new AnonymousAuthenticationToken(
                    "key",
                    "anonymous",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
                );
            }
        };
    }

    private boolean isValidToken(String token) {
        // 토큰 검증 로직
        return token != null && !token.isEmpty();
    }
}