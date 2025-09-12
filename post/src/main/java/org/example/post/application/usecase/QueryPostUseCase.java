package org.example.post.application.usecase;

import lombok.RequiredArgsConstructor;
import org.example.post.application.event.IncreasePostViewEvent;
import org.example.post.common.annotations.UseCase;
import org.example.post.domain.post.model.PostEntity;
import org.example.post.global.authentication.AuthenticatedUserProvider;
import org.example.post.global.exception.error.ErrorCodes;
import org.example.post.infrastructure.cache.service.GetLikeCountService;
import org.example.post.infrastructure.cache.service.GetViewCountService;
import org.example.post.domain.post.repository.PostJpaRepository;
import org.example.post.application.usecase.dto.response.PostDetailResponse;
import org.example.post.infrastructure.mq.kafka.application.viewlog.IncreasePostViewProducer;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryPostUseCase {

    private final PostJpaRepository postJpaRepository;

    private final IncreasePostViewProducer increasePostViewProducer;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final GetViewCountService getViewCountService;

    private final GetLikeCountService getLikeCountService;

    public PostDetailResponse execute(Long postId) { // todo 테스트 미완
        Long userId = authenticatedUserProvider.getCurrentUserId();
        increasePostViewProducer.publish(new IncreasePostViewEvent(postId, userId));

        Long viewCount = getViewCountService.getViewCounts(List.of(postId)).get(postId);
        Long likeCounts = getLikeCountService.getLikeCounts(List.of(postId)).get(postId);

        PostEntity post = postJpaRepository.findById(postId)
            .orElseThrow(ErrorCodes.POST_NOT_FOUND::throwException);

        return PostDetailResponse.of(post, likeCounts, viewCount);
    }
}
