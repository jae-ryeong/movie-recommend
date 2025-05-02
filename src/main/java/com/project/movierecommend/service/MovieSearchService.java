package com.project.movierecommend.service;

import com.project.movierecommend.domain.MovieDocument;
import com.project.movierecommend.repository.elasticsearch.MovieSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieSearchService {

    private final MovieSearchRepository movieSearchRepository;

    public List<MovieDocument> search(String title, String genre) {
        if (StringUtils.hasText(title) && StringUtils.hasText(genre)) {
            return movieSearchRepository.findByTitleContainingAndGenresContaining(title, genre);
        } else if (StringUtils.hasText(title)) {
            return movieSearchRepository.findByTitleContaining(title);
        } else if (StringUtils.hasText(genre)) {
            return movieSearchRepository.findByGenresContaining(genre);
        } else{
            return (List<MovieDocument>) movieSearchRepository.findAll();
        }
    }
}
