package com.project.movierecommend.controller;

import com.project.movierecommend.dto.UserActionDto;
import com.project.movierecommend.kafka.UserActionProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/actions")
public class UserActionController {

    private final UserActionProducer userActionProducer;

    @PostMapping
    public ResponseEntity<Void> sendUserAction(@RequestBody UserActionDto action) {
        userActionProducer.sendUserAction(action);
        return ResponseEntity.ok().build();
    }
}
