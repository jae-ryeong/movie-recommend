package com.project.movierecommend.controller;

import com.project.movierecommend.domain.Jpa.MovieEntity;
import com.project.movierecommend.repository.elasticsearch.RecommendationSearchRepository;
import com.project.movierecommend.repository.jpa.MovieEntityRepository;
import com.project.movierecommend.service.recommend.ContentBasedService;
import com.project.movierecommend.service.recommend.UserBasedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendationController {

    private final UserBasedService userBasedService;
    private final ContentBasedService contentBasedService;
    private final RecommendationSearchRepository recommendationSearchRepository;
    private final MovieEntityRepository movieEntityRepository;

    @GetMapping("/{userId}")
    public List<Long> recommend(@PathVariable("userId") Long userId,
                                @RequestParam(defaultValue = "10") int limit) {
        return userBasedService.recommendByViewMovie(userId, limit);
    }

    @GetMapping("/rating")
    public List<MovieEntity> recommend(@RequestParam("userId") Long userId) {
        return userBasedService.recommendByRatingSimilarity(userId, 10);
    }

    @GetMapping("/content")
    public List<MovieEntity> recommendContent(@RequestParam("userId") Long userId) {
        return contentBasedService.recommendByContent(userId, 10);
    }

    @GetMapping("/hybrid")
    public List<MovieEntity> recommendHybrid(@RequestParam("userId") Long userId) {
        return recommendationSearchRepository.findById(userId)
                .map(doc -> movieEntityRepository.findAllById(doc.getRecommendedMovieIds()))
                .orElse(Collections.emptyList());
    }
}
