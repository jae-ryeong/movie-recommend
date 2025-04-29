package com.project.movierecommend.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movies")
@Getter
@NoArgsConstructor
public class Movie {

    @Id
    private Long movieId;

    private String title;

    private String genres;

    public Movie(Long movieId, String title, String genres) {
        this.movieId = movieId;
        this.title = title;
        this.genres = genres;
    }
}
