package com.project.movierecommend.kafka;

import com.project.movierecommend.domain.Movie;
import com.project.movierecommend.repository.MovieSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {
    /*
     Kafka 토픽에서 메시지를 수신하고, 수신된 Movie 객체를 특정 리포지토리에 저장
     */
    private final MovieSearchRepository movieSearchRepository;

    /*  KafkaListener 어노테이션이 붙은 메소드는 Kafka 컨슈머 역할을 수행, 바이트 배열 형태의 메시지 데이터를 Movie 객체로 변환하여 이 메소드로 전달
        이 컨슈머가 리스닝(수신)할 Kafka 토픽의 이름을 지정, "movie-info" 토픽으로 전송된 메시지를 이 메소드가 받는다
        이 컨슈머가 속한 컨슈머 그룹의 ID를 지정
     */
    @KafkaListener(topics = "movie-info", groupId = "movie-group")
    public void consumeMovie(Movie movie) {
        log.info("Consumed movie from Kafka: {}", movie.getTitle());
        movieSearchRepository.save(movie);  //  Elasticsearch 인덱스에 문서를 저장
        log.info("Indexed movie into Elasticsearch: {}", movie.getTitle());
    }
}
