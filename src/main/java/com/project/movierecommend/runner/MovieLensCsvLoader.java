package com.project.movierecommend.runner;

import com.project.movierecommend.domain.MovieEntity;
import com.project.movierecommend.domain.Rating;
import com.project.movierecommend.repository.jpa.MovieEntityRepository;
import com.project.movierecommend.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
public class MovieLensCsvLoader implements CommandLineRunner {
    /*
    1. CommandLineRunner 인터페이스를 구현하는 클래스는 Spring Boot 애플리케이션이 완전히 초기화된 후에 run 메소드가 자동으로 실행
    2. 애플리케이션 시작 시점에 한 번 실행해야 하는 초기화 작업, 데이터 로딩 등에 유용하게 사용
     */

    private final MovieEntityRepository movieEntityRepository;
    private final JdbcTemplate jdbcTemplate;

    private final KafkaProducerService kafkaProducerService;

    /*
    앱 실행과 동시에 Kafka로 메시지를 보내서 "테스트 겸용"으로 사용 가능
    Kafka가 꺼져있으면 에러가 발생하므로 환경 설정 체크도 가능
     */

    @Override
    public void run(String... args) throws Exception {
        if (movieEntityRepository.count() == 0) {
            loadMovies();
            loadRatings();
            movieEntityRepository.findAll()
                    .forEach(kafkaProducerService::sendMovie);
        } else{
            System.out.println("DB에 영화 데이터가 이미 존재합니다. CSV 로딩 생략.");
        }
    }

    private void loadMovies() throws IOException {
        List<MovieEntity> batchList = new ArrayList<>();
        int batchSize = 100;
        long startTime = System.currentTimeMillis(); // 시작 시간

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
                    batchList.add(movieEntity);

                    if(batchList.size() >= batchSize){
                        insertMoviesInBatch(batchList, batchSize);
                        batchList.clear();
                    }
                }
            }

            // 마지막 배치
            if (!batchList.isEmpty()) {
                insertMoviesInBatch(batchList, batchSize);
            }

            long endTime = System.currentTimeMillis(); // 종료 시간
            long duration = endTime - startTime;

            System.out.println("movies CSV import completed in " + duration + " ms");
        }
    }

    private void loadRatings() throws IOException {
        List<Rating> batchList = new ArrayList<>();
        int batchSize = 1000;
        long startTime = System.currentTimeMillis(); // 시작 시간

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
                    batchList.add(rating);

                    if(batchList.size() >= batchSize){
                        insertRatingsInBatch(batchList, batchSize);
                        batchList.clear();
                    }
                }
            }

            // 마지막 배치
            if (!batchList.isEmpty()) {
                insertRatingsInBatch(batchList, batchSize);
            }

            long endTime = System.currentTimeMillis(); // 종료 시간
            long duration = endTime - startTime;

            System.out.println("ratings CSV import completed in " + duration + " ms");
        }
    }

    private void insertMoviesInBatch(List<MovieEntity> movies, int batchSize) {
        for (int i = 0; i < movies.size(); i += batchSize) {
            int end = Math.min(i + batchSize, movies.size());
            List<MovieEntity> batchList = movies.subList(i, end);

            jdbcTemplate.batchUpdate(
                    "INSERT INTO movierecommend_db.movies (movies.movie_id, title, genres) values (?,?,?)",
                    batchList,
                    batchSize,
                    (ps, movie) -> {
                        ps.setLong(1, movie.getMovieId());
                        ps.setString(2, movie.getTitle());
                        ps.setString(3, movie.getGenres());
                    }
            );
        }
    }

    private void insertRatingsInBatch(List<Rating> ratings, int batchSize) {
        for (int i = 0; i < ratings.size(); i += batchSize) {
            int end = Math.min(i + batchSize, ratings.size());
            List<Rating> batchList = ratings.subList(i, end);

            jdbcTemplate.batchUpdate(
                    "INSERT INTO movierecommend_db.ratings (user_id, movie_id, rating, timestamp) values (?,?,?,?)",
                    batchList,
                    batchSize,
                    (ps, rating) -> {
                        ps.setLong(1, rating.getUserId());
                        ps.setLong(2, rating.getMovieId());
                        ps.setFloat(3, rating.getRating());
                        ps.setLong(4, rating.getTimestamp());
                    }
            );
        }
    }
}
