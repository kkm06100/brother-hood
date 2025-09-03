package org.example.post.infrastructure.search.domain.post.repository;

import org.example.post.infrastructure.search.domain.post.document.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostSearchRepository extends ElasticsearchRepository<PostDocument, Long> {

}
