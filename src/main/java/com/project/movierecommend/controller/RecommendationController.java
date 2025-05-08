package com.project.movierecommend.controller;

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
        return recommendationService.recommendMoviesForUser(userId, limit);
    }
}
