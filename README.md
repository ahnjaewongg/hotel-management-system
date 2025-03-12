# 🏨 Hotel Management System

**Jakarta Servlet API와 Tomcat 기반 호텔 관리 CRM 시스템**  
  
객실 관리, 고객 관리, 예약 관리 등의 기능을 제공하며,  
직관적인 인터페이스를 통해 호텔 운영의 효율성을 높이는 것을 목표로 합니다.

---

## 📚 기술 스택

### 🔧 백엔드
![Java](https://img.shields.io/badge/Java_17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Jakarta Servlet](https://img.shields.io/badge/Jakarta_Servlet-E6322E?style=for-the-badge&logo=jakarta&logoColor=white)
![Tomcat](https://img.shields.io/badge/Apache_Tomcat-F8DC75?style=for-the-badge&logo=apache-tomcat&logoColor=black)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

### 🎨 프론트엔드
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![Bootstrap](https://img.shields.io/badge/Bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)

### 🛠️ 개발 도구
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

---

## 🌟 주요 기능

### 📊 기본 기능

| 기능 | 설명 |
|------|------|
| **사용자 관리** | 로그인/로그아웃, 회원가입, 관리자/일반 사용자 권한 구분 |
| **객실 관리** | 객실 목록 조회, 객실 추가/수정/삭제, 객실 상태 관리 (이용가능, 사용중, 청소중) |
| **고객 관리** | 고객 목록 조회, 고객 정보 추가/수정/삭제, 고객 검색 |
| **예약 관리** | 예약 생성, 예약 조회, 예약 수정 |

### 💡 차별화된 특징

#### 🔐 역할 기반 접근 제어
- 관리자와 일반 사용자 권한 분리
- 권한에 따른 기능 접근 제한
- 안전한 세션 관리

#### 📱 반응형 대시보드
- 직관적인 객실 상태 시각화
- 실시간 객실 상태 업데이트
- 모바일 친화적 인터페이스

#### ⚡ 효율적인 데이터 관리
- 싱글톤 패턴을 활용한 데이터베이스 연결 최적화
- 비동기 요청을 통한 빠른 사용자 경험
- 체계적인 데이터 구조화

---

## 🚀 설치 및 실행 방법

### 요구사항
- JDK 17 이상
- Maven
- MySQL 데이터베이스

<details>
<summary><b>설치 단계</b></summary>

1. 저장소 클론
   ```bash
   git clone https://github.com/yourusername/hotel-management.git
   cd hotel-management
   ```

2. 데이터베이스 설정
   ```sql
   CREATE DATABASE crm;
   ```

3. 프로젝트 빌드 및 실행
   ```bash
   mvn clean package
   java -jar target/customer-1.0-SNAPSHOT.jar
   ```

4. 웹 브라우저에서 접속
   ```
   http://localhost:8080
   ```
</details>

---

## 🔒 데이터베이스 설정

<details>
<summary>데이터베이스 연결 정보</summary>

기본 접속 정보:
- **URL**: `jdbc:mysql://localhost:3306/crm`
- **사용자**: `root`
- **비밀번호**: `1234`

이 설정은 `DatabaseManager.java` 파일에서 수정할 수 있습니다.
</details>

---

## 📁 프로젝트 구조
```
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

---

## 🧩 주요 클래스 설명

### 핵심 컴포넌트

#### 🚪 Main.java
- 애플리케이션의 진입점
- Tomcat 서버 설정 및 서블릿 매핑 구성

#### 🗄️ DatabaseManager.java
- 싱글톤 패턴으로 구현된 데이터베이스 연결 관리자
- 객실, 고객, 예약 관련 데이터베이스 작업 처리

#### 🌐 서블릿 클래스
- **LoginServlet**: 사용자 로그인 처리
- **DashboardServlet**: 메인 대시보드 페이지 및 객실 정보 제공
- **RoomManagementServlet**: 객실 관리 기능 (CRUD)
- **CustomerManagementServlet**: 고객 관리 기능 (CRUD)
- **ReservationServlet**: 예약 관리 기능

---

## 👥 사용자 역할

### 1. 관리자
- 모든 기능에 접근 가능
- 객실 관리, 고객 관리 등 시스템 관리 기능 사용

### 2. 일반 사용자
- 객실 조회 및 예약 기능 사용
- 자신의 예약 정보 관리

---

## 📊 데이터베이스 스키마

<details>
<summary>테이블 구조</summary>

### user 테이블
| 필드 | 타입 | 설명 |
|------|------|------|
| username | VARCHAR(50) | 사용자 아이디(PK) |
| name | VARCHAR(100) | 사용자 이름 |
| email | VARCHAR(100) | 이메일 주소 |
| password | VARCHAR(255) | 암호화된 비밀번호 |
| admin_yn | CHAR(1) | 관리자 여부(Y/N) |

### room 테이블
| 필드 | 타입 | 설명 |
|------|------|------|
| room_number | INT | 객실 번호(PK) |
| room_type | VARCHAR(50) | 객실 타입 |
| status | VARCHAR(20) | 객실 상태 |
| cleaning_status | VARCHAR(20) | 청소 상태 |

### reservation 테이블
| 필드 | 타입 | 설명 |
|------|------|------|
| id | INT | 예약 ID(PK) |
| name | VARCHAR(100) | 예약자 이름 |
| room_number | INT | 객실 번호(FK) |
| room_type | VARCHAR(50) | 객실 타입 |
| checkin_dt | DATE | 체크인 날짜 |
| checkout_dt | DATE | 체크아웃 날짜 |
| reservation_dt | DATETIME | 예약 일시 |
</details>

---

© 2023 Hotel Management System

<sub>이 프로젝트는 교육 목적으로 개발되었으며, 실제 서비스와는 관련이 없습니다.</sub>
