# BookMap

- [프론트엔드 바로가기](https://github.com/hyukjunkim1116/Bookmap-Front-Vue3)

---

## Stacks 🐈

#### Spring, Spring Boot

- Spring
  스프링에서 제공하는 기능을 바탕으로 비즈니스 로직 작성에 집중할 수 있습니다. 또한, 생성자 주입을 통해 의존성을 파악하기 위해 도입했습니다.
- Spring Boot
  스프링에서 프로젝트와 라이브러리 설정을 자동으로 설정하고 편리하게 이용하기 위해 도입했습니다.

#### Spring Security

- 웹 개발에서 필수적인 인증,인가 기능을 미리 제공하는 Spring Security를 활용함으로써 효과적이고 신속하게 보안 기능을 구현했습니다.

### Environment
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white)
![Github](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white)

### Config

### Development
![SpringBoot](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=Spring&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SpringSecurity](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)

### Communication
<img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white">

---

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
│   └── handler
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
