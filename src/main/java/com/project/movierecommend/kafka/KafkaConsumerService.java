package com.project.movierecommend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.movierecommend.domain.MovieDocument;
import com.project.movierecommend.domain.MovieEntity;
import com.project.movierecommend.repository.elasticsearch.MovieSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService { // 운영 중 실시간 데이터 수신 및 색인용 (Kafka → Elasticsearch)
    /*
     Kafka 토픽에서 메시지를 수신하고, 수신된 Movie 객체를 특정 리포지토리에 저장
     */
    private final MovieSearchRepository movieSearchRepository;
    private final ObjectMapper objectMapper;

    /*  KafkaListener 어노테이션이 붙은 메소드는 Kafka 컨슈머 역할을 수행, 바이트 배열 형태의 메시지 데이터를 Movie 객체로 변환하여 이 메소드로 전달
        이 컨슈머가 리스닝(수신)할 Kafka 토픽의 이름을 지정, "movie-info" 토픽으로 전송된 메시지를 이 메소드가 받는다
        이 컨슈머가 속한 컨슈머 그룹의 ID를 지정
     */
    @KafkaListener(topics = "movie-info", groupId = "movie-group", containerFactory = "movieEntityBatchFactory")  // 호출하지 않아도 Kafka에 메시지가 도착했을 때 자동으로 실행
    public void consumeMovie(List<String> messages) {
        log.info("Batch received: {}", messages.size());

        List<MovieEntity> movies = messages.stream()
                .map(message -> {
                    try {
                        return objectMapper.readValue(message, MovieEntity.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to deserialize MovieEntity", e);
                    }
                }).toList();

        List<MovieDocument> documents = movies.stream()
                .map(entity -> new MovieDocument(
                        entity.getMovieId(),
                        entity.getTitle(),
                        entity.getGenres()
                ))
                .toList();

        movieSearchRepository.saveAll(documents);  // bulk 색인, Elasticsearch 인덱스에 문서를 저장
        log.info("Batch indexing completed");
    }
}
