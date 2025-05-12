package com.project.movierecommend.domain.Elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "user-recommendations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDocument {

    @Id
    private Long userId;

    private List<Long> recommendedMovieIds;
}
