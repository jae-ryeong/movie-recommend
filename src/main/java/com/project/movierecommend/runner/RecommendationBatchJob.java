package com.project.movierecommend.runner;

import com.project.movierecommend.domain.Jpa.Rating;
import com.project.movierecommend.repository.elasticsearch.RecommendationSearchRepository;
import com.project.movierecommend.repository.jpa.RatingRepository;
import com.project.movierecommend.service.recommend.HybridRecommendationIndexer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RecommendationBatchJob implements ApplicationRunner {

    private final RatingRepository ratingRepository;
    private final HybridRecommendationIndexer hybridRecommendationIndexer;
    private final RecommendationSearchRepository recommendationSearchRepository;

    private static final int PAGE_SIZE = 500;   // 페이지당 유저 수
    private static final int THREAD_COUNT = 8;  // 병렬 처리 스레드 수

    @Override
    public void run(ApplicationArguments args) throws Exception {
        long beforeTime = System.currentTimeMillis();
        recommendationSearchRepository.deleteAll();
        // 전체 유저 수
        long totalUsers = ratingRepository.findAll().stream()
                .map(Rating::getUserId)
                .distinct()
                .count();

        int totalPages = (int) Math.ceil((double) totalUsers / PAGE_SIZE);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        for(int page = 0; page < totalPages; page++) {
            int currentPage = page;
            executor.submit(() -> {
                Pageable pageable = PageRequest.of(currentPage, PAGE_SIZE);
                List<Long> userIds = ratingRepository.findDistinctUserIds(pageable);
                hybridRecommendationIndexer.indexHybridRecommendationsBulk(userIds);
                System.out.printf("페이지 %d 인덱싱 완료 (유저 수: %d)%n", currentPage, userIds.size());
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);   // 스레드 풀이 종료될 때까지 대기
        long afterTime = System.currentTimeMillis();
        long secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
        System.out.println("시간차이(m) : "+secDiffTime);
        System.out.println("전체 인덱싱 완료");
    }
}
