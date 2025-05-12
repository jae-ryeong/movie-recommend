package com.project.movierecommend.service.recommend;

import com.project.movierecommend.domain.Jpa.MovieEntity;
import com.project.movierecommend.domain.Jpa.Rating;
import com.project.movierecommend.repository.jpa.MovieEntityRepository;
import com.project.movierecommend.repository.jpa.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentBasedService {

    private final RatingRepository ratingRepository;
    private final MovieEntityRepository movieEntityRepository;

    public List<MovieEntity> recommendByContent(Long userId, int limit){
        // 내가 평가한 영화 중 평점 4.0 이상 영화 목록 조회
        List<Rating> likedRatings  = ratingRepository.findByUserIdAndRatingGreaterThan(userId, 4.0f);
        if(likedRatings.isEmpty()) return Collections.emptyList();

        // 내가 본 영화 ID 목록 추출
        Set<Long> likedMovieIds = likedRatings.stream()
                .map(Rating::getMovieId)
                .collect(Collectors.toSet());

        // 영화 정보를 한번에 조회
        List<MovieEntity> likedMovies = movieEntityRepository.findAllById(likedMovieIds);

        // 장르 수집
        Set<String> preferredGenres = likedMovies.stream()
                .flatMap(movie -> Arrays.stream(movie.getGenres().split("\\|")))
                .collect(Collectors.toSet());

        // 유저가 본 영화 ID들 (중복 제외)

        // 전체 영화 조회
        List<MovieEntity> allMovies = movieEntityRepository.findAll();

        // 전체 영화에서 필터링
        return allMovies.stream()
                .filter(movie -> !likedMovieIds.contains(movie.getMovieId()))
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
