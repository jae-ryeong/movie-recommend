package com.project.movierecommend.service;

import com.project.movierecommend.domain.UserAction;
import com.project.movierecommend.repository.jpa.UserActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/*
    1. 사용자 행동 데이터를 기반으로 간단한 유사도 계산 (공통 영화 수)
    2. 추천 리스트 생성
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserActionRepository userActionRepository;

    public List<Long> recommendMoviesForUser(Long userId, int limit) {
        // 1. 전체 사용자 행동 조회
        List<UserAction> allActions = userActionRepository.findAll();

        // 2. 사용자별 영화 목록 구성
        HashMap<Long, Set<Long>> userMovieMap = new HashMap<>();    // 모든 사용자
        for (UserAction action : allActions) {
            // 기존 key 존재시 action.getMovieId()를 추가, key 없으면 새로운 HashSet을 생성하여 추가
            userMovieMap.computeIfAbsent(action.getUserId(), k -> new HashSet<>()).add(action.getMovieId());
        }

        // 3. 현재 사용자가 본 영화 목록
        // userId가 본 영화 목록을 userMovieMap에서 가져오고, 없으면 빈 Set 할당
        Set<Long> targetUserMovies = userMovieMap.getOrDefault(userId, Collections.emptySet()); // 내가 본 영화

        // 4. 유사 사용자 찾기 (공통 영화 수가 많은 사용자)
        HashMap<Long, Integer> similarityMap = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : userMovieMap.entrySet()) {  // HashMap.entrySet(): HashMap의 모든 key-value 쌍(Entry 객체)을 Set 형태로 반환
            Long otherUserId = entry.getKey();
            if(otherUserId.equals(userId)) continue;

            Set<Long> otherMovies = entry.getValue();   // 다른 사용자가 본 영화 목록
            HashSet<Long> common = new HashSet<>(targetUserMovies);
            common.retainAll(otherMovies);  // 두 사용자가 공통으로 본 영화 ID들만 common Set에 저장, 공통 영화 (common에서 otherMovies에 없는 요소 제거, 즉 교집합)

            similarityMap.put(otherUserId, common.size());
        }

        // 5. 유사도 높은 사용자 Top N 추출
        // 유사도 맵(similarityMap)을 유사도 점수(Value) 기준으로 내림차순 정렬
        List<Long> similarUsers = similarityMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)     // 사용자 ID만 추출
                .toList();

        // 6. 유사 사용자가 본 영화 중 내가 안 본 영화 추천
        Set<Long> recommended = new HashSet<>();
        for (Long similarUserId : similarUsers) {
            Set<Long> movies = userMovieMap.getOrDefault(similarUserId, Collections.emptySet());
            for (Long movieId : movies) {
                if(!targetUserMovies.contains(movieId)) {
                    recommended.add(movieId);   // 유사 사용자가 봤지만 나는 아직 안 본 영화를 추천
                }
            }
        }
        return recommended.stream().limit(limit).toList();
    }
}
