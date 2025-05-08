package com.project.movierecommend.kafka;

import com.project.movierecommend.dto.UserActionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionProducer {

    private final KafkaTemplate<String, UserActionDto> kafkaTemplate;

    private final static String TOPIC_NAME = "user-actions";

    public void sendUserAction(UserActionDto action) {
        kafkaTemplate.send(TOPIC_NAME, action);
        log.info("Sent user action {}", action);
    }
}
