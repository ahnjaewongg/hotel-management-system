# CRM (Customer Relationship Management) 시스템

## 프로젝트 개요
이 프로젝트는 호텔 관리를 위한 CRM 시스템으로, 객실 관리, 고객 관리, 예약 관리 등의 기능을 제공합니다. Jakarta Servlet API와 Tomcat을 사용하여 웹 애플리케이션으로 구현되었습니다.

## 기술 스택
- **백엔드**: Java, Jakarta Servlet API
- **프론트엔드**: HTML, CSS, JavaScript, Bootstrap
- **데이터베이스**: MySQL
- **서버**: Apache Tomcat
- **빌드 도구**: Maven

## 주요 기능
### 1. 사용자 관리
- 로그인/로그아웃
- 회원가입
- 관리자/일반 사용자 권한 구분

### 2. 객실 관리
- 객실 목록 조회
- 객실 추가/수정/삭제
- 객실 상태 관리 (이용가능, 사용중, 청소중)

### 3. 고객 관리
- 고객 목록 조회
- 고객 정보 추가/수정/삭제
- 고객 검색

### 4. 예약 관리
- 예약 생성
- 예약 조회
- 예약 수정

## 프로젝트 구조
```plaintext
src/
├── main/
│ ├── java/
│ │ └── com/
│ │ └── customer/
│ │ ├── constants/
│ │ │ └── CleaningStatus.java
│ │ ├── database/
│ │ │ └── DatabaseManager.java
│ │ ├── servlets/
│ │ │ ├── AdminCheckServlet.java
│ │ │ ├── CheckEmailServlet.java
│ │ │ ├── CheckUsernameServlet.java
│ │ │ ├── CustomerManagementServlet.java
│ │ │ ├── CustomerReservationServlet.java
│ │ │ ├── DashboardServlet.java
│ │ │ ├── LoginServlet.java
│ │ │ ├── LogoutServlet.java
│ │ │ ├── MainServlet.java
│ │ │ ├── ReservationServlet.java
│ │ │ ├── RoomDetailServlet.java
│ │ │ ├── RoomManagementServlet.java
│ │ │ ├── RoomOccupiedServlet.java
│ │ │ ├── SessionCheckServlet.java
│ │ │ └── SignupServlet.java
│ │ └── Main.java
│ └── resources/
│ ├── html/
│ │ ├── components/
│ │ │ ├── roomDetail.html
│ │ │ └── sidebar.html
│ │ ├── dashboard.html
│ │ ├── index.html
│ │ ├── login.html
│ │ └── signup.html
│ ├── static/
│ ├── css/
│ ├── js/
│ └── images/
```

### 설치 및 실행 방법

#### 1. 사전 요구사항
- JDK 17 이상
- Maven
- MySQL 데이터베이스

#### 2. 데이터베이스 설정
1. MySQL에 `crm` 데이터베이스 생성
2. 기본 접속 정보:
   - **URL**: `jdbc:mysql://localhost:3306/crm`
   - **사용자**: `root`
   - **비밀번호**: `1234`

#### 3. 프로젝트 빌드 및 실행
#### 4. 웹 브라우저에서 접속: `http://localhost:8080`

## 주요 클래스 설명

### 1. **Main.java**
- 애플리케이션의 진입점
- Tomcat 서버 설정 및 서블릿 매핑 구성

### 2. **DatabaseManager.java**
- 싱글톤 패턴으로 구현된 데이터베이스 연결 관리자
- 객실, 고객, 예약 관련 데이터베이스 작업 처리

### 3. **서블릿 클래스**
- **LoginServlet**: 사용자 로그인 처리
- **DashboardServlet**: 메인 대시보드 페이지 및 객실 정보 제공
- **RoomManagementServlet**: 객실 관리 기능 (CRUD)
- **CustomerManagementServlet**: 고객 관리 기능 (CRUD)
- **ReservationServlet**: 예약 관리 기능

## 사용자 역할
### 1. 관리자
- 모든 기능에 접근 가능
- 객실 관리, 고객 관리 등 시스템 관리 기능 사용

### 2. 일반 사용자
- 객실 조회 및 예약 기능 사용
- 자신의 예약 정보 관리

## 데이터베이스 스키마
- **user**: 사용자 정보 (`username`, `name`, `email`, `password`, `admin_yn`)
- **room**: 객실 정보 (`room_number`, `room_type`, `status`, `cleaning_status`)
- **reservation**: 예약 정보 (`id`, `name`, `room_number`, `room_type`, `checkin_dt`, `checkout_dt`, `reservation_dt`)
