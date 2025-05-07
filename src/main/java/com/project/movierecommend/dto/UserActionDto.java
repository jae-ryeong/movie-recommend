package com.project.movierecommend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActionDto {
    private Long userId;
    private Long movieId;
    private String action; // 예: "view", "like"
}
