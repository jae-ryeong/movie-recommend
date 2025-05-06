package com.project.movierecommend.runner;

import com.project.movierecommend.repository.jpa.MovieEntityRepository;
import com.project.movierecommend.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
public class MovieProducerRunner implements CommandLineRunner {

    /*
    앱 실행과 동시에 Kafka로 메시지를 보내서 "테스트 겸용"으로 사용 가능
    Kafka가 꺼져있으면 에러가 발생하므로 환경 설정 체크도 가능
     */
    private final KafkaProducerService kafkaProducerService;
    private final MovieEntityRepository movieEntityRepository;

    @Override
    public void run(String... args) throws Exception {
        movieEntityRepository.findAll()
                .forEach(kafkaProducerService::sendMovie);
    }
}
