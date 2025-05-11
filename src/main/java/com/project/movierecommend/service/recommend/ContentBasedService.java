package com.project.movierecommend.service.recommend;

import com.project.movierecommend.domain.MovieEntity;
import com.project.movierecommend.domain.Rating;
import com.project.movierecommend.repository.jpa.MovieEntityRepository;
import com.project.movierecommend.repository.jpa.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ContentBasedService {

    private final RatingRepository ratingRepository;
    private final MovieEntityRepository movieEntityRepository;

    public List<MovieEntity> recommendByContent(Long userId, int limit) {
        // 내가 평가한 영화 중 평점 4.0 이상 영화 목록 조회
        List<Rating> likedRatings  = ratingRepository.findByUserIdAndRatingGreaterThan(userId, 4.0f);
        if(likedRatings.isEmpty()) return Collections.emptyList();

        Set<String> preferredGenres = new HashSet<>();
        List<Long> seenMovieIds  = new ArrayList<>();   // 내가 본 영화의 id List

        for (Rating rating : likedRatings) {
            seenMovieIds.add(rating.getMovieId());

            movieEntityRepository.findById(rating.getMovieId()).ifPresent(movie-> {
                String[] genres = movie.getGenres().split("\\|");   // \\|는 정규식에서 | 문자
                preferredGenres.addAll(Arrays.asList(genres));  // 선호하는 영화의 장르 추가
            });
        }

        Set<MovieEntity> recommended = new HashSet<>();
        for (String genre : preferredGenres) {
            List<MovieEntity> candidates = movieEntityRepository.findByMovieIdNotInAndGenresContaining(seenMovieIds,genre); // 내가 본 영화가 아니면서 장르는 포함하는
            recommended.addAll(candidates);
        }

        return recommended.stream()
                .limit(limit)
                .toList();
    }

    public List<Long> recommendMovieIds(Long userId, int limit) {
        return recommendByContent(userId, limit).stream()
                .map(MovieEntity::getMovieId)
                .toList();
    }
}
