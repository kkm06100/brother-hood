package brother.hood.auth.infrastructure.grpc.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import brother.hood.auth.persistence.User;
import brother.hood.auth.persistence.repository.UserJpaRepository;
import storties.auth.grpc.AuthRequest;
import storties.auth.grpc.AuthResponse;
import storties.auth.grpc.AuthServiceGrpc;


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