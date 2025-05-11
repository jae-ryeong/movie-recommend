package com.project.movierecommend.domain.Jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_actions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserAction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long movieId;
    private String action;
    private LocalDateTime timestamp;
}
