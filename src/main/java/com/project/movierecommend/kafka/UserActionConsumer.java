package com.project.movierecommend.kafka;

import com.project.movierecommend.domain.UserAction;
import com.project.movierecommend.dto.UserActionDto;
import com.project.movierecommend.repository.jpa.UserActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserActionConsumer {

    private final UserActionRepository userActionRepository;

    @KafkaListener(topics = "user-actions", groupId = "movie-group")
    public void consume(UserActionDto actionDto) {
        System.out.println("Kafka 메세지 수신: " + actionDto);

        UserAction userAction = new UserAction();
        userAction.builder()
                .userId(actionDto.getUserId())
                .movieId(actionDto.getMovieId())
                .action(actionDto.getAction())
                .timestamp(LocalDateTime.now());

        userActionRepository.save(userAction);
    }
}
