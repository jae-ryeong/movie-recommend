package com.project.movierecommend.repository.jpa;

import com.project.movierecommend.domain.Jpa.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieEntityRepository extends JpaRepository<MovieEntity, Long> {
    List<MovieEntity> findByGenresContaining(String genre);
    List<MovieEntity> findByMovieIdNotInAndGenresContaining(List<Long> excludeIds, String genre);
}
