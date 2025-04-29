package com.project.movierecommend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ratings")
@Getter
@NoArgsConstructor
public class Rating {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long movieId;
    private Float rating;
    private Long timestamp;

    public Rating(Long userId, Long movieId, Float rating, Long timestamp) {
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
        this.timestamp = timestamp;
    }
}
