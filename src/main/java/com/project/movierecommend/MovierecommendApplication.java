 package com.project.movierecommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/*
	1. 실행시 csv 파일의 데이터를 db에 저장
	2. db에 저장된 영화 데이터를 Kafka 토픽으로 전송 (Producer)
	3. Kafka 메시지 수신 후 Elasticsearch에 저장(색인) (Consumer)
 */

@SpringBootApplication
@EnableJpaRepositories("com.project.movierecommend.repository.jpa")
@EnableElasticsearchRepositories("com.project.movierecommend.repository.elasticsearch")
public class MovierecommendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovierecommendApplication.class, args);
	}

}
