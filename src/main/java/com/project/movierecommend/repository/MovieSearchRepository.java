package com.project.movierecommend.repository;

import com.project.movierecommend.domain.Movie;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MovieSearchRepository extends ElasticsearchRepository<Movie, Long> {
    /*
    	<Elasticsearch에 저장될 도메인 객체(문서)의 타입, Movie의 Id 타입>
    	JPARepository와의 차이점으로는
    	1. J는 RDBMS에 저장, E는 Elasticsearch에 저장
    	2. J는 테이블 기반, E는 JSON 기반
    	3. J는 트랜잭션 지원, E는 트랜잭션 지원 X
     */
}
