 package com.project.movierecommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/*
	1. 실행시 csv 파일의 데이터를 db에 저장 후 kafka 토픽으로 전송 (MovieLensCsvLoader -> MovieProducerRunner)
	2. Kafka 메시지 수신 후 @KafkaListner에 의해 자동으로 Elasticsearch에 저장(색인) (Consumer) (KafkaProducerService)
	---
	3. REST API → Kafka Producer → Kafka Broker → Kafka Consumer → MySQL 저장
 */

@SpringBootApplication
@EnableJpaRepositories("com.project.movierecommend.repository.jpa")	// JPA Repository가 위치한 패키지를 스캔
@EnableElasticsearchRepositories("com.project.movierecommend.repository.elasticsearch") // Elasticsearch 전용 리포지토리 스캔
public class MovierecommendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovierecommendApplication.class, args);
	}

}
