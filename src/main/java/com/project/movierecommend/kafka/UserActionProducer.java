package com.project.movierecommend.kafka;

import com.project.movierecommend.dto.UserActionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActionProducer {

    private final KafkaTemplate<String, UserActionDto> kafkaTemplate;

    private final static String TOPIC_NAME = "user-actions";

    public void sendUserAction(UserActionDto action) {
        kafkaTemplate.send(TOPIC_NAME, action);
    }
}
