package com.project.movierecommend.service;

import com.project.movierecommend.domain.MovieEntity;
import com.project.movierecommend.domain.Rating;
import com.project.movierecommend.repository.jpa.MovieEntityRepository;
import com.project.movierecommend.repository.jpa.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MovieLensCsvLoader implements CommandLineRunner {
    /*
    1. CommandLineRunner 인터페이스를 구현하는 클래스는 Spring Boot 애플리케이션이 완전히 초기화된 후에 run 메소드가 자동으로 실행
    2. 애플리케이션 시작 시점에 한 번 실행해야 하는 초기화 작업, 데이터 로딩 등에 유용하게 사용
     */

    private final MovieEntityRepository movieEntityRepository;
    private final RatingRepository ratingRepository;

    @Override
    public void run(String... args) throws Exception {
        loadMovies();
        loadRatings();
    }

    private void loadMovies() throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/data/movies.csv"))) {
            reader.readLine();  // 헤더 건너뛰기

            String line;
            while ((line = reader.readLine()) != null) {
                /*
                 쉼표(,)로만 split하면 영화 제목에 쉼표가 포함된 경우 문제가 발생
                 따옴표 안의 쉼표는 무시, 따옴표 밖의 쉼표만 기준으로 분리
                 */
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                // 분리된 필드가 최소 3개 이상인지 확인, 유효하지 않은 줄은 패스
                if (parts.length >= 3) {
                    Long movieId = Long.parseLong(parts[0]);
                    String title = parts[1].replace("\"", "");
                    String genres = parts[2];

                    MovieEntity movieEntity = new MovieEntity(movieId, title, genres);
                    movieEntityRepository.save(movieEntity);
                }
            }
        }
    }

    private void loadRatings() throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/data/ratings.csv"))) {
            reader.readLine();

            String line;
            while( (line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 4) {
                    Long userId = Long.parseLong(parts[0]);
                    Long movieId = Long.parseLong(parts[1]);
                    Float ratingData = Float.parseFloat(parts[2]);
                    Long timestamp = Long.parseLong(parts[3]);

                    Rating rating = new Rating(userId,movieId, ratingData, timestamp);
                    ratingRepository.save(rating);
                }
            }
        }
    }
}
