# MovieRecommend – 영화 추천 시스템

## 프로젝트 개요
영화 추천 시스템을 직접 설계 및 구현한 프로젝트로서 무비렌즈(MovieLens) 데이터를 기반으로 사용자 행동을 수집하고, 추천 알고리즘을 통해 개인화된 영화 추천 결과를 제공합니다.

## 기술 스택

### 백엔드
- **Spring Boot**: REST API 개발

### 기타
- **Docker**: 컨테이너 기반 환경 구성 및 서비스 배포 자동화
- **MysqlDB**: 영화 메타데이터 및 사용자 행동 데이터를 저장하는 관계형 데이터베이스
- **Elasticsearch**: 콘텐츠 기반 검색을 위한 색인 및 유사도 분석 수행
- **Kafka**: 사용자 행동 데이터를 비동기 메시지 스트림 형태로 처리하여 확장성과 실시간성 확보

## 주요 기능
- 무비렌즈 CSV 데이터를 MySQL, Elasticsearch에 이중 저장
- Kafka를 활용한 사용자 행동 이벤트 전송 및 비동기 처리
- 협업 필터링(User-based CF) 기반 영화 추천 알고리즘 구현
- 추천 영화 ID를 Elasticsearch에서 검색하여 상세 정보 제공
- REST API 기반 추천 결과 제공
- Docker Compose로 Kafka, MySQL, Elasticsearch 통합 환경 구축
