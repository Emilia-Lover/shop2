-- =====================================================
--  SHOP2 데이터베이스 스크립트 (MariaDB)
--  HeidiSQL 같은 도구에서 root 계정으로 실행하세요.
-- =====================================================

-- 1. 데이터베이스와 전용 계정 만들기 (처음 한 번만)
CREATE DATABASE IF NOT EXISTS shop2db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'shop2user'@'localhost' IDENTIFIED BY 'shop1234';
GRANT ALL PRIVILEGES ON shop2db.* TO 'shop2user'@'localhost';
FLUSH PRIVILEGES;

USE shop2db;

-- 2. 테이블 만들기
-- ⚠️ 아래 DROP 문은 기존 테이블과 데이터를 모두 지워요. 처음 설치할 때만 실행하세요.
DROP TABLE IF EXISTS cart_item;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS member;

-- 회원
CREATE TABLE member (
                        id           BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '회원 번호',
                        username     VARCHAR(100) NOT NULL UNIQUE            COMMENT '로그인 아이디 (SNS 회원은 provider_고유번호)',
                        password     VARCHAR(255)                            COMMENT '암호화된 비밀번호 (SNS 회원은 NULL)',
                        name         VARCHAR(50)  NOT NULL                   COMMENT '이름',
                        email        VARCHAR(100)                            COMMENT '이메일',
                        role         VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER' COMMENT '권한',
                        provider     VARCHAR(20)  NOT NULL DEFAULT 'local'   COMMENT '가입 경로: local, google, naver, kakao',
                        provider_id  VARCHAR(100)                            COMMENT 'SNS 고유 번호',
                        created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일'
);

-- 상품
CREATE TABLE product (
                         id           BIGINT        AUTO_INCREMENT PRIMARY KEY COMMENT '상품 번호',
                         brand        VARCHAR(50)   NOT NULL                   COMMENT '브랜드',
                         name         VARCHAR(100)  NOT NULL                   COMMENT '상품 이름',
                         price        INT           NOT NULL                   COMMENT '가격 (원)',
                         stock        INT           NOT NULL DEFAULT 0         COMMENT '재고 수량',
                         description  TEXT                                     COMMENT '상품 설명',
                         image_url    VARCHAR(500)                             COMMENT '상품 이미지 주소',
                         created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일'
);

-- 장바구니
CREATE TABLE cart_item (
                           id          BIGINT    AUTO_INCREMENT PRIMARY KEY COMMENT '장바구니 항목 번호',
                           member_id   BIGINT    NOT NULL                   COMMENT '회원 번호',
                           product_id  BIGINT    NOT NULL                   COMMENT '상품 번호',
                           quantity    INT       NOT NULL DEFAULT 1         COMMENT '수량',
                           created_at  DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '담은 날짜',
                           UNIQUE KEY uk_cart_member_product (member_id, product_id),
                           CONSTRAINT fk_cart_member  FOREIGN KEY (member_id)  REFERENCES member(id)  ON DELETE CASCADE,
                           CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- 3. 샘플 가전제품 8개 (브랜드는 모두 가상 브랜드)
INSERT INTO product (brand, name, price, stock, description, image_url) VALUES
                                                                            ('루미텍', '55인치 4K UHD 스마트 TV', 649000, 12,
                                                                             '선명한 4K 화질과 HDR을 지원하는 55인치 스마트 TV예요.\n유튜브, 넷플릭스 같은 앱을 리모컨 하나로 바로 볼 수 있어요.',
                                                                             'https://placehold.co/600x600/e8f0fe/2f6fed?text=4K+Smart+TV'),
                                                                            ('프레시랩', '양문형 냉장고 836L', 1290000, 5,
                                                                             '4인 이상 가족도 넉넉한 836L 대용량 양문형 냉장고예요.\n정수기와 얼음 디스펜서가 함께 들어 있어요.',
                                                                             'https://placehold.co/600x600/e6f4ea/1e8e3e?text=Refrigerator'),
                                                                            ('클린웨이브', '드럼 세탁기 21kg', 899000, 8,
                                                                             '이불 빨래도 한 번에 되는 21kg 대용량 드럼 세탁기예요.\nAI 세탁 코스가 옷감에 맞춰 물과 세제 양을 알아서 조절해요.',
                                                                             'https://placehold.co/600x600/e3f2fd/1565c0?text=Washing+Machine'),
                                                                            ('클린웨이브', '무선 스틱 청소기', 329000, 20,
                                                                             '1.5kg 가벼운 무게에 최대 60분 사용할 수 있는 무선 청소기예요.\n먼지통 자동 비움 스테이션이 함께 들어 있어요.',
                                                                             'https://placehold.co/600x600/f3e8fd/7b1fa2?text=Cordless+Vacuum'),
                                                                            ('쿡메이트', '대용량 에어프라이어 5.5L', 89000, 35,
                                                                             '기름 없이 바삭한 요리를 할 수 있는 5.5L 에어프라이어예요.\n치킨 한 마리가 통째로 들어가요.',
                                                                             'https://placehold.co/600x600/fff4e5/e65100?text=Air+Fryer'),
                                                                            ('하이온', 'AI 로봇청소기', 459000, 0,
                                                                             '집 구조를 스스로 기억해서 구석구석 청소하는 로봇청소기예요.\n(품절 표시 테스트용 상품이에요)',
                                                                             'https://placehold.co/600x600/eceff1/455a64?text=Robot+Vacuum'),
                                                                            ('에어핏', '공기청정기 (거실용 62㎡)', 239000, 15,
                                                                             '넓은 거실도 빠르게 정화하는 공기청정기예요.\n미세먼지 농도를 색깔 불빛으로 한눈에 보여줘요.',
                                                                             'https://placehold.co/600x600/e0f7fa/00838f?text=Air+Purifier'),
                                                                            ('쿡메이트', '전자레인지 23L', 119000, 25,
                                                                             '버튼 하나로 데우기, 해동이 되는 23L 전자레인지예요.\n안쪽이 법랑 코팅이라 청소가 쉬워요.',
                                                                             'https://placehold.co/600x600/fce4ec/c2185b?text=Microwave');