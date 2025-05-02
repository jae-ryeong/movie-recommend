package com.project.movierecommend.repository.elasticsearch;

import com.project.movierecommend.domain.MovieDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MovieSearchRepository extends ElasticsearchRepository<MovieDocument, Long> {
    /*
    	<Elasticsearch에 저장될 도메인 객체(문서)의 타입, Movie의 Id 타입>
    	JPARepository와의 차이점으로는
    	1. J는 RDBMS에 저장, E는 Elasticsearch에 저장
    	2. J는 테이블 기반, E는 JSON 기반
    	3. J는 트랜잭션 지원, E는 트랜잭션 지원 X
     */

    List<MovieDocument> findByTitleContainingAndGenresContaining(String title, String genre);
    List<MovieDocument> findByGenresContaining(String genre);
    List<MovieDocument> findByTitleContaining(String title);
}
