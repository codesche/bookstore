-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bookstore;

-- 기존 테이블 삭제 (재실행 시)
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS member;

-- 회원 테이블 생성
CREATE TABLE member (
    -- 회원 ID (UUID v7 형식의 문자열)
                        member_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '회원 고유 ID',

    -- 회원 정보
                        email VARCHAR(100) NOT NULL UNIQUE COMMENT '이메일 (로그인 ID)',
                        password VARCHAR(255) NOT NULL COMMENT '비밀번호 (암호화 저장)',
                        name VARCHAR(50) NOT NULL COMMENT '회원 이름',
                        phone VARCHAR(20) COMMENT '전화번호',

    -- 회원 상태
                        status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '회원 상태 (ACTIVE, INACTIVE, DELETED)',

    -- 시간 정보 (Instant 타입을 TIMESTAMP로 저장)
                        created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시간',
                        updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시간',

    -- 인덱스 설정 (조회 성능 향상)
                        INDEX idx_email (email),
                        INDEX idx_status (status),
                        INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회원 정보 테이블';

-- 도서 테이블 생성
CREATE TABLE book (
    -- 도서 ID (UUID v7 형식의 문자열)
                      book_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '도서 고유 ID',

    -- 도서 기본 정보
                      title VARCHAR(200) NOT NULL COMMENT '도서 제목',
                      author VARCHAR(100) NOT NULL COMMENT '저자',
                      isbn VARCHAR(20) NOT NULL UNIQUE COMMENT 'ISBN (국제 표준 도서 번호)',
                      publisher VARCHAR(100) COMMENT '출판사',

    -- 도서 상세 정보
                      price INT NOT NULL COMMENT '가격',
                      stock_quantity INT NOT NULL DEFAULT 0 COMMENT '재고 수량',
                      description TEXT COMMENT '도서 설명',
                      category VARCHAR(50) COMMENT '카테고리',

    -- 도서 상태
                      status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '도서 상태 (AVAILABLE, OUT_OF_STOCK, DISCONTINUED)',

    -- 시간 정보 (Instant 타입을 TIMESTAMP로 저장)
                      published_at TIMESTAMP(6) COMMENT '출판일',
                      created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '등록 시간',
                      updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시간',

    -- 인덱스 설정 (대규모 트래픽 대비 성능 최적화)
                      INDEX idx_title (title),
                      INDEX idx_author (author),
                      INDEX idx_isbn (isbn),
                      INDEX idx_category (category),
                      INDEX idx_status (status),
                      INDEX idx_price (price),
                      INDEX idx_created_at (created_at),

    -- 복합 인덱스 (자주 함께 조회되는 컬럼)
                      INDEX idx_category_status (category, status),
                      INDEX idx_status_stock (status, stock_quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='도서 정보 테이블';

-- 테스트 데이터 삽입
INSERT INTO member (member_id, email, password, name, phone, status, created_at, updated_at) VALUES
                                                                                                 ('01935e3a-0001-7000-8000-000000000001', 'test1@example.com', '$2a$10$encrypted_password', '홍길동', '010-1234-5678', 'ACTIVE', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                                                                                                 ('01935e3a-0002-7000-8000-000000000002', 'test2@example.com', '$2a$10$encrypted_password', '김철수', '010-2345-6789', 'ACTIVE', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                                                                                                 ('01935e3a-0003-7000-8000-000000000003', 'test3@example.com', '$2a$10$encrypted_password', '이영희', '010-3456-7890', 'INACTIVE', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

INSERT INTO book (book_id, title, author, isbn, publisher, price, stock_quantity, description, category, status, published_at, created_at, updated_at) VALUES
                                                                                                                                                           ('01935e3a-1001-7000-8000-000000000001', 'Effective Java', 'Joshua Bloch', '9780134685991', '인사이트', 36000, 50, 'Java 프로그래밍 필독서', 'IT', 'AVAILABLE', '2018-01-01 00:00:00.000000', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                                                                                                                                                           ('01935e3a-1002-7000-8000-000000000002', 'Clean Code', 'Robert C. Martin', '9780132350884', '인사이트', 33000, 30, '클린 코드 작성법', 'IT', 'AVAILABLE', '2013-12-24 00:00:00.000000', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                                                                                                                                                           ('01935e3a-1003-7000-8000-000000000003', 'Refactoring', 'Martin Fowler', '9780134757599', '한빛미디어', 35000, 0, '리팩토링 2판', 'IT', 'OUT_OF_STOCK', '2020-04-01 00:00:00.000000', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                                                                                                                                                           ('01935e3a-1004-7000-8000-000000000004', 'Design Patterns', 'Gang of Four', '9780201633612', '프리렉', 54000, 20, '디자인 패턴 바이블', 'IT', 'AVAILABLE', '2015-03-15 00:00:00.000000', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                                                                                                                                                           ('01935e3a-1005-7000-8000-000000000005', 'Spring in Action', 'Craig Walls', '9781617294945', '제이펍', 42000, 15, 'Spring Framework 실전 가이드', 'IT', 'AVAILABLE', '2022-01-01 00:00:00.000000', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

-- 테이블 정보 확인
SHOW TABLES;
DESCRIBE member;
DESCRIBE book;

-- 데이터 확인
SELECT * FROM member;
SELECT * FROM book;