package com.project.movierecommend.service.recommend;

import com.project.movierecommend.domain.Elasticsearch.RecommendationDocument;
import com.project.movierecommend.domain.Jpa.MovieEntity;
import com.project.movierecommend.repository.elasticsearch.RecommendationSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class HybridRecommendationIndexer {

    private final RecommendationSearchRepository recommendationSearchRepository;
    private final ContentBasedService contentBasedService;
    private final UserBasedService userBasedService;

    // 실시간 한 명의 유저 추천 메서드
    public void indexHybridRecommendation(Long userId) {
        // 1. 추천 결과 생성
        List<Long> cosIds = userBasedService.recommendMovieIds(userId, 5);
        List<Long> contentIds = contentBasedService.recommendMovieIds(userId, 5);

        // 2. 중복 제거
        Set<Long> merged = new LinkedHashSet<>();
        merged.addAll(cosIds);
        merged.addAll(contentIds);

        // 3. 문서 생성 및 저장
        RecommendationDocument doc = new RecommendationDocument(
                userId,
                new ArrayList<>(merged)
        );

        recommendationSearchRepository.save(doc);
    }

    // 전체 배치 작업으로 추천 메서드
    public List<RecommendationDocument> buildHybridDocs(List<Long> userIds) {
        List<RecommendationDocument> docs = new ArrayList<>();

        for (Long userId : userIds) {
            List<Long> cosIds = userBasedService.recommendMovieIds(userId, 5);
            List<Long> contentIds = contentBasedService.recommendMovieIds(userId, 5);

            Set<Long> merged = new LinkedHashSet<>();
            merged.addAll(cosIds);
            merged.addAll(contentIds);

            RecommendationDocument doc = new RecommendationDocument(
                    userId,
                    new ArrayList<>(merged)
            );
            docs.add(doc);
        }
        return docs;
    }

    public void indexHybridRecommendationsBulk(List<Long> userIds) {
        List<RecommendationDocument> documents = buildHybridDocs(userIds);
        recommendationSearchRepository.saveAll(documents); // Bulk 삽입
    }
}
