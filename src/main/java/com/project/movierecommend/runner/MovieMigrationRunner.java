package com.project.movierecommend.runner;

import com.project.movierecommend.service.MovieMigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieMigrationRunner implements CommandLineRunner {

    private final MovieMigrationService movieMigrationService;
    @Override
    public void run(String... args) throws Exception {
        movieMigrationService.migrateToElasticsearch(); //  Elasticsearch로 영화 데이터 마이그레이션
    }
}
