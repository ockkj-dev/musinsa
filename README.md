# 과제 설명

## 요구사항
1. 각 요구사항에 맞는 API 를 구현
2. 해당 API 를 어떻게 사용하는지 방법 제공
3. 코드 빌드, 테스트, 실행 방법 제공


### 구현범위
1. 요구사항에 맞는 API
2. API 확인할 수 있는 시각화 프론트 페이지
3. 간단한 unit, integration test
4. h2 database 를 활용한 in-memory database


### 코드 빌드 및 테스트, 실행
1. 해당 git 을 clone 
2. 해당 프로젝트 내 musinsa 라는 폴더로 프로젝트 open
3. gradle 로 dependency isntall (./gradlew build)
4. 테스트 진행 (./gradlew test)
5. 테스트 완료 후 application 실행 (spring boot 프로젝트로 configuration 자동생성 또는 MusinsaApplication.java 실행)


### API 테스트
1. 코드 실행 후 localhost:8080/ 으로 접근
2. 프론트 페이지에서 각 api 테스트 진행
3. 추가, 수정, 삭제 의 경우 마지막 전체 상품을 조회하는 api 로 변경사항 테스트 진행


### 전달사항
1. 카테고리의 경우 상의 -> TOP, 하의 -> BOTTOM 과 같은 영문 코드로 사용하려 했으나, 해당 과제의 직관성을 위하여 한글로 작성
2. 개인 작업으로 인한 단일 브랜치 운영 (main - develop)