package org.example.post.application.usecase;

import lombok.RequiredArgsConstructor;
import org.example.post.application.event.IncreasePostLikeEvent;
import org.example.post.common.annotations.UseCase;
import org.example.post.global.authentication.AuthenticatedUserProvider;
import org.example.post.infrastructure.mq.kafka.event.like.increase.IncreasePostLikeProducer;

@UseCase
@RequiredArgsConstructor
public class AddLikeUseCase {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final IncreasePostLikeProducer increasePostLikeProducer;

    public void execute(Long postId) {
        Long userId = authenticatedUserProvider.getCurrentUserId();

        increasePostLikeProducer.publish(IncreasePostLikeEvent
            .builder()
            .postId(postId)
            .userId(userId)
            .build());
    }
}
