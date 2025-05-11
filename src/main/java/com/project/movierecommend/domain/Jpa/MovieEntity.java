package com.project.movierecommend.domain.Jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

//@Document(indexName = "movies") // Elasticsearch의 어떤 인덱스에 매핑되는지를 지정
@Entity
@Table(name = "movies")
@Getter
@NoArgsConstructor
public class MovieEntity {

    @Id
    private Long movieId;

    private String title;

    private String genres;

    public MovieEntity(Long movieId, String title, String genres) {
        this.movieId = movieId;
        this.title = title;
        this.genres = genres;
    }
}
