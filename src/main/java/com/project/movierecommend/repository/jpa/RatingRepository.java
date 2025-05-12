package com.project.movierecommend.repository.jpa;

import com.project.movierecommend.domain.Jpa.Rating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByUserId(Long userId);
    List<Rating> findByMovieIdIn(List<Long> movieId);
    List<Rating> findByUserIdAndRatingGreaterThan(Long userId, float minRating);

    @Query("select distinct r.userId from Rating r")
    List<Long> findDistinctUserIds(Pageable pageable);
}
