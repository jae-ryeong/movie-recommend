package com.project.movierecommend.repository.jpa;

import com.project.movierecommend.domain.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieEntityRepository extends JpaRepository<MovieEntity, Long> {
}
