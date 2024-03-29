# BookMap

- [프론트엔드 바로가기](https://github.com/hyukjunkim1116/Bookmap-Front-Vue3)

## 배포 주소

## 프로젝트 소개

## 시작 가이드

---

## Requirements

---

## Stacks 🐈

### Environment
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white)
![Github](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white)

### Config

### Development
<https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=Spring&logoColor=white>
<https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white>
<https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white>

### Communication
<img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white">

---

## 화면 구성 📺

---

## 주요 기능 📦

### 회원가입,로그인

### 게시글 작성, 댓글 작성

### 좋아요,싫어요,구독,검색

### 결제

### 채팅,알림

### 신고

### 책 검색

### 매장 찾기

---

## 아키텍쳐

### 디렉토리 구조

```
├── V2Application.java
├── book
│   ├── BookController.java
│   ├── BookResponseItem.java
│   ├── BookService.java
│   └── dto
├── comment
│   ├── controller
│   ├── domain
│   ├── dto
│   ├── repository
│   └── service
├── config
│   ├── AmazonS3Config.java
│   ├── AppConfig.java
│   ├── QueryDslConfig.java
│   ├── SecurityConfig.java
│   ├── SecurityWebSocketConfig.java
│   ├── SwaggerConfig.java
│   ├── WebClientConfig.java
│   ├── WebDriverConfig.java
│   ├── WebMvcConfig.java
│   ├── WebSocketConfig.java
│   ├── filter
│   ├── handler
│   └── interceptor
├── event
│   ├── handler
│   ├── listener
│   └── publisher
├── exception
│   ├── ErrorResponse.java
│   ├── ExceptionController.java
│   ├── FoodMapException.java
│   ├── comment
│   ├── email
│   ├── jwt
│   ├── post
│   └── user
├── jwt
│   ├── JwtResponseDTO.java
│   ├── JwtService.java
│   ├── RefreshToken.java
│   └── RefreshTokenRequestDTO.java
├── kakao
│   ├── domain
│   ├── dto
│   └── service
├── payment
│   ├── controller
│   ├── domain
│   ├── dto
│   ├── repository
│   └── service
├── post
│   ├── controller
│   ├── domain
│   ├── dto
│   ├── repository
│   └── service
├── report
│   ├── Report.java
│   ├── ReportController.java
│   ├── ReportRepository.java
│   ├── ReportRequestDTO.java
│   └── ReportService.java
├── user
│   ├── controller
│   ├── domain
│   ├── dto
│   ├── repository
│   └── service
├── util
│   ├── AmazonS3
│   ├── BookCrawler.java
│   ├── DistanceCalculator.java
│   ├── FaviconController.java
│   ├── ImageResponseDTO.java
│   ├── LocationData.java
│   └── email
└── websocket
    ├── controller
    ├── domain
    ├── dto
    ├── handler
    ├── manager
    ├── repository
    └── service
```

## API 명세

### [API 명세](https://denim-knot-470.notion.site/ef06589a8f5e49529645cff63419abc0?v=4a2368990f7043059aa5be52b6abb633&pvs=4)
