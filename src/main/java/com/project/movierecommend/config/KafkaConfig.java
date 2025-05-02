package com.project.movierecommend.config;

import com.project.movierecommend.domain.MovieDocument;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    // ProducerFactory는 Kafka Producer 인스턴스를 생성하는 역할
    @Bean
    public ProducerFactory<String, MovieDocument> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // Kafka 브로커의 연결 정보를 설정하는 키, Kafka 브로커가 로컬 머신의 9092 포트에서 실행되고 있음을 지정
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // 메시지의 Key를 직렬화(Serialize)하는 클래스를 설정하는 키
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 메시지의 Value를 직렬화하는 클래스를 설정하는 키, Movie객체는 JSON타입으로 직렬화
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, MovieDocument> kafkaTemplate() {
        // 보통 <String, String>이지만 <String, Movie>로 타입을 지정해주면서 타입 안정성이 높아졌다.
        return new KafkaTemplate<>(producerFactory());
    }
}
