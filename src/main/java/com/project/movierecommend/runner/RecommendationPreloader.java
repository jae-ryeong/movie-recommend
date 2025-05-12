package com.project.movierecommend.runner;

import com.project.movierecommend.domain.Jpa.MovieEntity;
import com.project.movierecommend.domain.Jpa.Rating;
import com.project.movierecommend.repository.jpa.MovieEntityRepository;
import com.project.movierecommend.repository.jpa.RatingRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationPreloader {

    @Getter
    private Map<Long, List<Rating>> ratingsByUser;
    @Getter
    private List<MovieEntity> cachedAllMovies;
    private final RatingRepository ratingRepository;
    private final MovieEntityRepository movieEntityRepository;

    @PostConstruct
    public void init() {
        List<Rating> allRatings = ratingRepository.findAll();
        ratingsByUser = allRatings.stream()
                .collect(Collectors.groupingBy(Rating::getUserId));

        cachedAllMovies = movieEntityRepository.findAll();
    }
}
