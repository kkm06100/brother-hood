package brother.hood.auth.infrastructure.grpc.service;

import brotherhood.auth.grpc.AuthRequest;
import brotherhood.auth.grpc.AuthResponse;
import brotherhood.auth.grpc.AuthServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import brother.hood.auth.persistence.User;
import brother.hood.auth.persistence.repository.UserJpaRepository;


@GrpcService
@RequiredArgsConstructor
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    private final UserJpaRepository userJpaRepository;

    @Override
    public void authenticate(AuthRequest request, StreamObserver<AuthResponse> responseObserver) {
        Long id = request.getUserId();

        Optional<User> userOpt = userJpaRepository.findById(id);

        if (userOpt.isEmpty()) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("User not found")
                    .asRuntimeException()
            );
            return;
        }

        User user = userOpt.get();

        AuthResponse response = AuthResponse.newBuilder()
            .setUserId(user.getId())
            .setEmail(user.getEmail())
            .setRole(String.valueOf(user.getRole()))
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}