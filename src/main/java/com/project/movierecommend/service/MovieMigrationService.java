package com.project.movierecommend.service;

import com.project.movierecommend.domain.MovieDocument;
import com.project.movierecommend.domain.MovieEntity;
import com.project.movierecommend.repository.elasticsearch.MovieSearchRepository;
import com.project.movierecommend.repository.jpa.MovieEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieMigrationService {

    private final MovieEntityRepository movieEntityRepository;
    private final MovieSearchRepository movieSearchRepository;

    public void migrateToElasticsearch() {
        List<MovieEntity> movieEntities = movieEntityRepository.findAll();

        List<MovieDocument> documents = movieEntities.stream()
                .map(entity -> new MovieDocument(
                        entity.getMovieId(),
                        entity.getTitle(),
                        entity.getGenres()
                )).toList();

        movieSearchRepository.saveAll(documents);
    }
}
