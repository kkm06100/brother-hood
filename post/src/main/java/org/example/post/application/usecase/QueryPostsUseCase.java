package org.example.post.application.usecase;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.post.application.usecase.dto.response.PostsResponse;
import org.example.post.common.annotations.UseCase;
import org.example.post.domain.post.QueryPostRepository;
import org.example.post.domain.post.model.PostEntity;
import org.example.post.infrastructure.cache.service.GetLikeCountService;
import org.example.post.infrastructure.cache.service.GetViewCountService;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryPostsUseCase {

    private final QueryPostRepository queryPostRepository;

    private final GetViewCountService getViewCountService;

    private final GetLikeCountService getLikeCountService;

    public PostsResponse execute(int page) {
        List<PostEntity> post = queryPostRepository.queryPostWithPaging(page);
        List<Long> postIds = post.stream().map(PostEntity::getId).toList();

        Map<Long,Long> likeCounts = getLikeCountService.getLikeCounts(postIds);
        Map<Long, Long> viewCounts = getViewCountService.getViewCounts(postIds);

        return PostsResponse.ofEntity(post, likeCounts, viewCounts);
    }
}
