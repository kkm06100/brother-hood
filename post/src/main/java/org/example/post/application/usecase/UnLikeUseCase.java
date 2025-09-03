package org.example.post.application.usecase;

import lombok.RequiredArgsConstructor;
import org.example.post.application.event.DecreasePostLikeEvent;
import org.example.post.common.annotations.UseCase;
import org.example.post.domain.post.QueryPostRepository;
import org.example.post.global.authentication.AuthenticatedUserProvider;
import org.example.post.infrastructure.mq.kafka.event.like.decrease.DecreasePostLikeProducer;

@UseCase
@RequiredArgsConstructor
public class UnLikeUseCase {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final DecreasePostLikeProducer decreasePostLikeProducer;

    private final QueryPostRepository queryPostRepository;

    public void execute(Long postId) {
        Long userId = authenticatedUserProvider.getCurrentUserId();
        queryPostRepository.queryPostById(postId);

        decreasePostLikeProducer.publish(DecreasePostLikeEvent
            .builder()
            .postId(postId)
            .userId(userId)
            .build());
    }
}
