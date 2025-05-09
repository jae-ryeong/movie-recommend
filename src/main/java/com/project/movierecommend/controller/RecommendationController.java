package com.project.movierecommend.controller;

import com.project.movierecommend.domain.MovieEntity;
import com.project.movierecommend.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public List<Long> recommend(@PathVariable("userId") Long userId,
                                @RequestParam(defaultValue = "10") int limit) {
        return recommendationService.recommendByViewMovie(userId, limit);
    }

    @GetMapping("/rating")
    public List<MovieEntity> recommend(@RequestParam("userId") Long userId){
        return recommendationService.recommendByRatingSimilarity(userId, 10);
    }
}
