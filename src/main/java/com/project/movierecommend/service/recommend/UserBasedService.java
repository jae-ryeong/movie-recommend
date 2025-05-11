package com.project.movierecommend.service.recommend;

import com.project.movierecommend.domain.MovieEntity;
import com.project.movierecommend.domain.Rating;
import com.project.movierecommend.domain.UserAction;
import com.project.movierecommend.repository.jpa.MovieEntityRepository;
import com.project.movierecommend.repository.jpa.RatingRepository;
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
public class UserBasedService {

    private final UserActionRepository userActionRepository;
    private final MovieEntityRepository movieEntityRepository;
    private final RatingRepository ratingRepository;

    // 조회 영화 기반 협업 필터링
    public List<Long> recommendByViewMovie(Long userId, int limit) {
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

    // User-based(평점) 기반 협업 필터링
    public List<MovieEntity> recommendByRatingSimilarity(Long targetUserId, int limit) {
        // 1. 전체 평점 데이터
        List<Rating> allRatings = ratingRepository.findAll();
        // 2. 유저별 평점 맵
        Map<Long, Map<Long, Float>> userRatingsMap = new HashMap<>();
        for (Rating r : allRatings) {
            userRatingsMap
                    .computeIfAbsent(r.getUserId(), k -> new HashMap<>()) // 해당 유저 ID가 맵에 없으면 새 HashMap 생성
                    .put(r.getMovieId(), r.getRating());                         // 해당 유저의 영화 ID와 평점 추가
        }
        // 3. 타겟 유저(자신) 평점
        Map<Long, Float> targetRatings = userRatingsMap.get(targetUserId);
        if (targetRatings == null) return Collections.emptyList();
        // 4. 유사도 계산
        Map<Long, Double> similarityScroes = new HashMap<>();
        for (Map.Entry<Long, Map<Long, Float>> entry : userRatingsMap.entrySet()) {
            Long otherUserId = entry.getKey();
            if(otherUserId.equals(targetUserId)) continue;

            double sim = cosineSimilarity(targetRatings, entry.getValue()); // 자신과 다른 유저의 유사도 계산

            if (sim > 0.0) {    // 유사도가 0보다 큰 경우에만 유사도 맵에 저장
                similarityScroes.put(otherUserId, sim);
            }
        }
        System.out.println("similarityScroes.size() = " + similarityScroes.size());
        // 5. 유사한 유저가 좋아한 영화 수집
        Map<Long, Double> movieScoreMap = new HashMap<>();
        for (Map.Entry<Long, Double> entry : similarityScroes.entrySet()) {     // 유사도 점수가 계산된 모든 유저
            Long similarUserId = entry.getKey();
            Double simScore = entry.getValue();   // 유사도 점수

            // 해당 유사 유저의 평점 데이터
            Map<Long, Float> similarUserRatings = userRatingsMap.get(similarUserId);

            // 유사 유저가 평점을 매긴 모든 영화를 순회
            for (Map.Entry<Long, Float> ratingEntry : similarUserRatings.entrySet()) {
                Long movieId = ratingEntry.getKey();
                if(targetRatings.containsKey(movieId)) continue;    // 이미 본 영화는 제외

                /*
                    - 해당 영화에 대한 예상 평점을 계산하여 누적
                    - 예상 평점 = Sum (유사도 * 유사 유저의 해당 영화 평점)
                    - merge 메소드는 키가 이미 존재하면 기존 값에 새 값을 더하고, 없으면 새 값을 추가
                 */
                movieScoreMap.merge(movieId, simScore * ratingEntry.getValue(), Double::sum);
            }
        }
        // 6. 상위 영화 추천
        return movieScoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())  // 누적 예상 평점(Value)을 기준으로 내림차순 정렬
                .limit(limit)
                .map(entry -> movieEntityRepository.findById(entry.getKey()).orElse(null))  // 영화 ID(Key)를 추출, 해당 ID로 MovieEntity를 조회
                .filter(Objects::nonNull)   // 조회된 MovieEntity 중 null이 아닌 것만 필터링, DB에 없으면 제외
                .toList();  // List 형태로 반환
    }

    //movieId만 뽑는 메서드
    public List<Long> recommendMovieIds(Long userId, int limit) {
        return recommendByRatingSimilarity(userId, limit).stream()
                .map(MovieEntity::getMovieId)
                .toList();
    }

    /*
     - 코사인 유사도 계산
     - Long은 영화 ID, Float는 평점이나 선호도 점수
     - 1에 가까울수록 유사하다
     */
    private double cosineSimilarity(Map<Long, Float> a, Map<Long, Float> b){
        Set<Long> commonKeys = new HashSet<>(a.keySet());
        commonKeys.retainAll(b.keySet());

        // 공통키가 하나도 없다면 유사도 0
        if (commonKeys.isEmpty()) return 0.0;

        double dotProduct = 0.0;    // 두 벡터의 내적 (분자)
        double normA = 0.0;         // 벡터 a의 크기의 제곱 (분모 계산시 사용)
        double normB = 0.0;         // 벡터 b의 크기의 제곱 (분모 계산시 사용)

        // 공통 키에 해당하는 값들로 내적 계산
        for (Long key : commonKeys) {
            dotProduct += a.get(key) * b.get(key);
        }

        for (float v : a.values()) normA += v * v;
        for (float v : b.values()) normB += v * v;

        /*
            코사인 유사도 값을 계산하여 반환합니다.
            공식: (A · B) / (||A|| * ||B||)
            A · B 는 dotProduct
            ||A|| 는 sqrt(normA)
            ||B|| 는 sqrt(normB)
         */
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
