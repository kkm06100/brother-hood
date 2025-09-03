package org.example.post.domain.post.repository;

import org.example.post.domain.post.model.PostEntity;
import org.springframework.data.repository.CrudRepository;

public interface PostJpaRepository extends CrudRepository<PostEntity, Long> {
}
