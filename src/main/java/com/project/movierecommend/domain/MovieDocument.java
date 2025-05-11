package com.project.movierecommend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "movies") // Elasticsearch의 어떤 인덱스에 매핑되는지를 지정
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MovieDocument {
    // 영화 자체의 정보 저장

    @Id
    private Long movieId;

    private String title;

    private String genres;
}
