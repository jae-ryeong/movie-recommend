package com.project.movierecommend.repository.elasticsearch;

import com.project.movierecommend.domain.RecommendationDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RecommendationSearchRepository extends ElasticsearchRepository<RecommendationDocument, Long> {
}
