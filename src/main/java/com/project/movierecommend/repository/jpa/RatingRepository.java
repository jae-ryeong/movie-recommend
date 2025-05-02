package com.project.movierecommend.repository.jpa;

import com.project.movierecommend.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
