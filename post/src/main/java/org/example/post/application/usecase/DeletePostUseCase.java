package org.example.post.application.usecase;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.post.application.event.DeletePostEvent;
import org.example.post.common.annotations.UseCase;
import org.example.post.infrastructure.mq.kafka.event.post.delete.DeletePostProducer;
import org.example.post.global.authentication.AuthenticatedUserProvider;
import org.example.post.global.exception.error.ErrorCodes;
import org.example.post.infrastructure.client.grpc.user.dto.AuthenticatedUser;
import org.example.post.domain.post.CommandPostRepository;
import org.example.post.domain.post.QueryPostRepository;
import org.example.post.domain.post.model.PostEntity;

@UseCase
@RequiredArgsConstructor
public class DeletePostUseCase {

    private final CommandPostRepository commandPostRepository;

    private final QueryPostRepository queryPostRepository;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final DeletePostProducer deletePostProducer;

    public void execute(Long postId) {
        PostEntity postEntity = queryPostRepository.queryPostById(postId);
        AuthenticatedUser authenticatedUser = authenticatedUserProvider.getAuthenticatedUser();

        if(Objects.equals(authenticatedUser.userId(), postEntity.getId())) {
           throw ErrorCodes.POST_DELETE_FORBIDDEN.throwException();
        }

        commandPostRepository.deletePostByPostId(postId);

        deletePostProducer.publish(new DeletePostEvent(postEntity.getId()));
    }
}
