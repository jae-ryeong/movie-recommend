package com.project.movierecommend.service.recommend;

import com.project.movierecommend.domain.Jpa.MovieEntity;
import com.project.movierecommend.domain.Jpa.Rating;
import com.project.movierecommend.runner.RecommendationPreloader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentBasedService {

    private final RecommendationPreloader recommendationPreloader;

    // 평점 기반 영화 추천
    public List<MovieEntity> recommendByContent(Long userId, int limit) {
        List<Rating> likedRatings = recommendationPreloader.getLikedRatingsByUser(userId);
        if (likedRatings.isEmpty()) return Collections.emptyList();

        // 내가 본 영화의 ID
        Set<Long> seenMovieIds = likedRatings.stream()
                .map(Rating::getMovieId)
                .collect(Collectors.toSet());

        // 내가 본 영화 Entity
        List<MovieEntity> likedMovies = recommendationPreloader.getCachedAllMovies().stream()
                .filter(m -> seenMovieIds.contains(m.getMovieId()))
                .toList();

        // 내가 본 영화의 장르 Set
        Set<String> preferredGenres = likedMovies.stream()
                .flatMap(movie -> Arrays.stream(movie.getGenres().split("\\|")))
                .collect(Collectors.toSet());

        // 최종 영화 추천
        return recommendationPreloader.getCachedAllMovies().stream()
                .filter(movie -> !seenMovieIds.contains(movie.getMovieId()))
                .filter(movie -> preferredGenres.stream()
                        .anyMatch(genre -> movie.getGenres().contains(genre)))
                .limit(limit)
                .toList();
    }
    public List<Long> recommendMovieIds(Long userId, int limit) {
        return recommendByContent(userId, limit).stream()
                .map(MovieEntity::getMovieId)
                .toList();
    }
}
