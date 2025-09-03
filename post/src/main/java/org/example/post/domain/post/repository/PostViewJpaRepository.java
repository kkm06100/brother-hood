package org.example.post.domain.post.repository;

import org.example.post.domain.post.model.PostViewEntity;
import org.springframework.data.repository.CrudRepository;

public interface PostViewJpaRepository extends CrudRepository<PostViewEntity, Long> {
}
