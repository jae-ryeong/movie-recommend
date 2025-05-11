package com.project.movierecommend.repository.jpa;

import com.project.movierecommend.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByUserId(Long userId);
    List<Rating> findByMovieIdIn(List<Long> movieId);
    List<Rating> findByUserIdAndRatingGreaterThan(Long userId, float minRating);
}
