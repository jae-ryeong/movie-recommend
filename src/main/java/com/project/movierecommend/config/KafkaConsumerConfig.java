package com.project.movierecommend.config;

import com.project.movierecommend.domain.Jpa.MovieEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MovieEntity> movieEntityBatchFactory(ConsumerFactory<String, MovieEntity> consumerFactory){
        // @KafkaListener 어노테이션이 붙은 메소드를 감지하고, 해당 리스너를 실행할 컨테이너(스레드 풀 또는 태스크 실행기)를 생성하는 역할
        ConcurrentKafkaListenerContainerFactory<String, MovieEntity> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        return factory;

    }
}
