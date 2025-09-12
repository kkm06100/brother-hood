package org.example.post.application.usecase;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.post.application.event.CreatePostEvent;
import org.example.post.common.annotations.UseCase;
import org.example.post.infrastructure.mq.kafka.application.post.create.CreatePostProducer;
import org.example.post.global.authentication.AuthenticatedUserProvider;
import org.example.post.infrastructure.client.grpc.auth.dto.AuthenticatedUser;
import org.example.post.domain.post.CommandPostRepository;
import org.example.post.domain.post.model.PostEntity;
import org.example.post.application.usecase.dto.request.CreatePostRequest;

@UseCase
@RequiredArgsConstructor
public class CreatePostUseCase {

    private final CommandPostRepository commandPostRepository;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final CreatePostProducer createPostProducer;

    public void execute(CreatePostRequest request) {

        AuthenticatedUser user = authenticatedUserProvider.getAuthenticatedUser();

        PostEntity postEntity = commandPostRepository.savePost(PostEntity
            .builder()
            .userId(user.userId())
            .tags(request.tags())
            .title(request.title())
            .content(request.content())
            .isPublished(request.isPublished())
            .updatedAt(LocalDateTime.now())
            .build());

        createPostProducer.publish(CreatePostEvent.from(postEntity));
    }
}
