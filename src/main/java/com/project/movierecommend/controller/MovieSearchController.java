package com.project.movierecommend.controller;

import com.project.movierecommend.domain.MovieDocument;
import com.project.movierecommend.service.MovieSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieSearchController {

    private final MovieSearchService movieSearchService;

    @GetMapping("/search")
    public List<MovieDocument> searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre
    ){
        return movieSearchService.search(title, genre);
    }
}
