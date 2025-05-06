package com.project.movierecommend.service;

import com.project.movierecommend.domain.MovieEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {
    /*
        Movie 객체 정보를 특정 토픽으로 전송하는 프로듀서(Producer) 역할을 하는 부분
        Key를 사용하면 같은 Key를 가진 메시지가 같은 파티션으로 전송되도록 보장
        결국 Movie 객체를 "movie-info"라는 이름의 Kafka 토픽으로 비동기적으로 전송하는 기능
     */
    private final KafkaTemplate<String, MovieEntity> kafkaTemplate;

    private static final String TOPIC = "movie-info";

    // 색인된 데이터를 kafka로 전송
    public void sendMovie(MovieEntity movieEntity) {
        log.info("Producing movie to Kafka: {}", movieEntity.getTitle());
        kafkaTemplate.send(TOPIC, movieEntity);
    }
}
