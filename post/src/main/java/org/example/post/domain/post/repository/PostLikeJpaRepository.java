package org.example.post.domain.post.repository;

import org.example.post.domain.post.model.PostLikeEntity;
import org.springframework.data.repository.CrudRepository;

public interface PostLikeJpaRepository extends CrudRepository<PostLikeEntity, Long> {
}
