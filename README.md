# SHOP2 - 가전제품 쇼핑몰

Spring Boot + MyBatis + MariaDB로 만든 가전제품 쇼핑몰 연습 프로젝트예요.

## 주요 기능

- **회원가입**: 입력값 검사, 아이디 중복 확인, BCrypt 비밀번호 암호화
- **로그인**: 일반 로그인 + SNS 로그인 (구글, 네이버, 카카오)
    - SNS로 처음 로그인하면 자동으로 회원가입
- **상품**: 상품 목록, 상품 상세, 품절 표시
- **장바구니**: 담기, 목록, 수량 변경(−/+ 버튼), 삭제, 총 금액 계산
    - 같은 상품을 다시 담으면 수량이 합쳐짐
    - 재고보다 많이 담을 수 없음
- **마이페이지**: 회원 정보 확인
- **404 화면**: 없는 페이지 안내

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.10, Spring Security, OAuth2 Client |
| Persistence | MyBatis 4.0.1 |
| Database | MariaDB |
| View | Thymeleaf |
| Build | Gradle |

## 실행 방법

### 1. 데이터베이스 준비

MariaDB에 root 계정으로 접속해서 `db/schema.sql`을 실행해요.
(데이터베이스 `shop2db`, 계정 `shop2user`, 테이블, 샘플 상품이 만들어져요)

### 2. SNS 로그인 키 설정

`src/main/resources/application-secret.properties.example`을 복사해서
같은 폴더에 `application-secret.properties`로 저장하고, 각 개발자 사이트에서 발급받은 값을 채워요.

| SNS | 리다이렉트(Callback) URI |
|---|---|
| 구글 | `http://localhost:8080/login/oauth2/code/google` |
| 네이버 | `http://localhost:8080/login/oauth2/code/naver` |
| 카카오 | `http://localhost:8080/login/oauth2/code/kakao` |

### 3. 실행

```bash
./gradlew bootRun
```

브라우저에서 http://localhost:8080 에 접속해요.

## 프로젝트 구조

```
src/main/java/com/example/shop2
 ├─ config       보안 설정 (SecurityConfig)
 ├─ controller   화면 요청 처리 (Home, Member, Product, Cart)
 ├─ domain       DB 테이블과 1:1 클래스 (Member, Product, CartItem)
 ├─ dto          화면용 클래스 (MemberJoinForm, CartItemView)
 ├─ mapper       MyBatis 매퍼 인터페이스
 ├─ security     로그인 처리 (일반 + SNS)
 └─ service      비즈니스 로직

src/main/resources
 ├─ mapper       MyBatis SQL (XML)
 ├─ static       CSS, JS
 └─ templates    Thymeleaf 화면
```