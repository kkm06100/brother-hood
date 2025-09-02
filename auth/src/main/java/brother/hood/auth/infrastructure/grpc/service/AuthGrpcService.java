package brother.hood.auth.infrastructure.grpc.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import brother.hood.grpc.AuthRequest;
import brother.hood.grpc.AuthResponse;
import brother.hood.grpc.AuthServiceGrpc;
import brother.hood.grpc.GetEmailsRequest;
import brother.hood.grpc.GetEmailsResponse;
import brother.hood.grpc.UserEmail;
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

    @Override
    public void getEmails(GetEmailsRequest request, StreamObserver<GetEmailsResponse> responseObserver) {
        List<Long> userIds = request.getUserIdsList();

        if (userIds.isEmpty()) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("User IDs list cannot be empty")
                    .asRuntimeException()
            );
            return;
        }

        List<User> users = (List<User>) userJpaRepository.findAllById(userIds);

        GetEmailsResponse.Builder responseBuilder = GetEmailsResponse.newBuilder();

        for (User user : users) {
            UserEmail userEmail = UserEmail.newBuilder()
                .setUserId(user.getId())
                .setEmail(user.getEmail())
                .build();

            responseBuilder.addUserEmails(userEmail);
        }

        GetEmailsResponse response = responseBuilder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}