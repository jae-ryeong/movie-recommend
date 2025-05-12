package com.project.movierecommend.runner;

import com.project.movierecommend.domain.Jpa.MovieEntity;
import com.project.movierecommend.domain.Jpa.Rating;
import com.project.movierecommend.repository.jpa.MovieEntityRepository;
import com.project.movierecommend.repository.jpa.RatingRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
    private Map<Long, List<Rating>> likedRatingsByUser;

    private final RatingRepository ratingRepository;
    private final MovieEntityRepository movieEntityRepository;

    @PostConstruct
    public void init() {
        List<Rating> allRatings = ratingRepository.findAll();

        ratingsByUser = allRatings.stream()
                .collect(Collectors.groupingBy(Rating::getUserId));

        likedRatingsByUser = allRatings.stream()
                .filter(r -> r.getRating() > 4.0f)
                .collect(Collectors.groupingBy(Rating::getUserId));

        cachedAllMovies = movieEntityRepository.findAll();
    }

    public List<Rating> getLikedRatingsByUser(long userId) {
        return likedRatingsByUser.getOrDefault(userId, Collections.emptyList());
    }
}
