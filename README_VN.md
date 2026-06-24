# 🎯 Hệ Thống Đặt Sân Đá Bóng - Hướng Dẫn Sử Dụng

## 📋 Mục Lục
1. [Tính Năng Chính](#tính-năng-chính)
2. [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
3. [Cài Đặt & Chạy](#cài-đặt--chạy)
4. [Cấu Trúc Dự Án](#cấu-trúc-dự-án)
5. [Chi Tiết Chức Năng](#chi-tiết-chức-năng)
6. [API Endpoints](#api-endpoints)

---

## 🎮 Tính Năng Chính

### ✅ Đã Hoàn Thành
- **🔐 Hệ Thống Xác Thực**
  - Đăng ký tài khoản mới
  - Đăng nhập an toàn với mã hóa BCrypt
  - Đăng xuất
  - Kiểm soát quyền truy cập (Role-based)

- **👤 Quản Lý Tài Khoản**
  - Lưu thông tin người dùng (tên, email, số điện thoại)
  - Phân quyền (CUSTOMER, STAFF, ADMIN)
  - Session management

- **🎨 Giao Diện Người Dùng**
  - Thiết kế hiện đại, năng động
  - Responsive design (Mobile-friendly)
  - Chủ đề thể thao sinh động
  - Hiệu ứng animation mượt mà

### 🚀 Sắp Triển Khai
- Tìm kiếm sân bóng
- Quản lý đặt sân
- Thanh toán online
- Đánh giá & bình luận
- Chia sẻ với bạn bè
- Thống kê & thành tích

---

## 📦 Yêu Cầu Hệ Thống

### Bắt Buộc
- **Java JDK 21+**
- **MySQL 8.0+**
- **Maven 3.8+**
- **Spring Boot 4.0.6**

### Cài Đặt
```bash
# Windows
java -version  # Kiểm tra Java
mysql --version  # Kiểm tra MySQL
```

---

## 🚀 Cài Đặt & Chạy

### 1. Chuẩn Bị Database

```sql
-- Tạo database
CREATE DATABASE datsanjava CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Spring Boot sẽ tự động tạo bảng từ entities
```

### 2. Cấu Hình Kết Nối Database

File: `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/datsanjava?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=123456
```

### 3. Chạy Ứng Dụng

```bash
# Từ command line
mvn spring-boot:run

# Hoặc build file JAR
mvn clean package
java -jar target/datsan-0.0.1-SNAPSHOT.jar
```

### 4. Truy Cập Ứng Dụng

```
🌐 URL: http://localhost:8080
📝 Đăng nhập: http://localhost:8080/login
🆕 Đăng ký: http://localhost:8080/register
```

---

## 📁 Cấu Trúc Dự Án

```
src/main/java/com/tanh/datsan/
├── entity/
│   ├── Account.java          (Tài khoản người dùng)
│   ├── Role.java             (Enum vai trò)
│   ├── Booking.java          (Đơn đặt sân)
│   ├── Pitch.java            (Thông tin sân)
│   ├── Payment.java          (Thanh toán)
│   └── TimeSlot.java         (Khung giờ)
│
├── repository/
│   └── AccountRepository.java (JPA Repository)
│
├── service/
│   └── AuthService.java      (Logic xác thực)
│
├── controller/
│   └── AuthController.java   (Xử lý request)
│
├── security/
│   └── UserDetailsServiceImpl.java (Chi tiết người dùng)
│
└── config/
    └── SecurityConfig.java   (Cấu hình bảo mật)

src/main/resources/templates/
├── login.html                (Trang đăng nhập)
├── register.html             (Trang đăng ký)
└── dashboard.html            (Bảng điều khiển)
```

---

## 🔧 Chi Tiết Chức Năng

### 🔐 Đăng Ký Tài Khoản

**URL:** `POST /register`

**Form Fields:**
```
- fullName (Bắt buộc): Họ và tên
- email (Bắt buộc): Địa chỉ email
- phone (Tùy chọn): Số điện thoại
- username (Bắt buộc): Tên đăng nhập độc lập
- password (Bắt buộc): Mật khẩu
- confirmPassword (Bắt buộc): Xác nhận mật khẩu
```

**Xác Thực:**
- ✓ Tên đăng nhập phải duy nhất
- ✓ Email phải duy nhất
- ✓ Mật khẩu và xác nhận phải khớp
- ✓ Tất cả trường bắt buộc phải điền

**Kết Quả:** Lưu vào database, chuyển hướng về trang đăng nhập

---

### 🔑 Đăng Nhập

**URL:** `POST /login`

**Form Fields:**
```
- username: Tên đăng nhập
- password: Mật khẩu
```

**Bảo Mật:**
- ✓ Mật khẩu được mã hóa bằng BCrypt
- ✓ Session management tự động
- ✓ CSRF protection bật

**Kết Quả Thành Công:** Chuyển hướng đến `/dashboard`

---

### 📊 Dashboard

**URL:** `GET /dashboard` (Yêu cầu đăng nhập)

**Hiển Thị:**
- ✓ Tên người dùng
- ✓ Thống kê: Số sân, lượt đặt, đánh giá
- ✓ Các tính năng chính
- ✓ Nút đăng xuất

---

## 🌐 API Endpoints

| Phương Thức | URL | Mô Tả | Yêu Cầu Xác Thực |
|-------------|-----|-------|-----------------|
| GET | / | Chuyển hướng đến /login | ❌ |
| GET | /login | Trang đăng nhập | ❌ |
| POST | /login | Xử lý đăng nhập | ❌ |
| GET | /register | Trang đăng ký | ❌ |
| POST | /register | Xử lý đăng ký | ❌ |
| GET | /dashboard | Bảng điều khiển | ✅ |
| GET | /logout | Đăng xuất | ✅ |

---

## 🎨 Thiết Kế & UX

### Màu Sắc
- 🔵 Đăng nhập: Xanh dương (Sports theme)
- 🔴 Đăng ký: Hồng gradient (Dynamic)
- 🟣 Dashboard: Tím gradient (Premium)

### Hiệu Ứng
- ✨ Animation khi load trang
- 🎯 Hover effects trên buttons
- 📱 Responsive trên mobile

### Icons
- ⚽ Football theme
- 📍 Location
- 📅 Calendar
- 💳 Payment
- ⭐ Rating

---

## 🔒 Bảo Mật

### Triển Khai
1. **BCrypt Password Encoding**
   - Mật khẩu được mã hóa trước lưu DB
   - Không thể giải mã ngược

2. **Spring Security**
   - Role-based access control
   - CSRF protection
   - Session management

3. **Database**
   - Unique constraints trên username, email
   - NOT NULL constraints
   - Relationships được quản lý

### Cần Triển Khai
- [ ] HTTPS/SSL
- [ ] Rate limiting (Chống brute force)
- [ ] Email verification
- [ ] Password recovery
- [ ] Two-factor authentication

---

## 🐛 Troubleshooting

### Lỗi: "Connection refused" (MySQL)
```
✓ Đảm bảo MySQL đang chạy
✓ Kiểm tra credentials trong application.properties
✓ Kiểm tra port (default: 3306)
```

### Lỗi: "Table 'datsanjava' doesn't exist"
```
✓ Spring Boot sẽ tự tạo bảng
✓ Kiểm tra spring.jpa.hibernate.ddl-auto=update
✓ Restart ứng dụng
```

### Lỗi: "Username or password incorrect" (Đăng nhập)
```
✓ Kiểm tra lại tên đăng nhập/mật khẩu
✓ Đảm bảo đã đăng ký tài khoản
✓ Kiểm tra database có account
```

---

## 📚 Dependencies

### Spring Boot Starters
```xml
- spring-boot-starter-web (Web MVC)
- spring-boot-starter-data-jpa (Database ORM)
- spring-boot-starter-security (Authentication)
- spring-boot-starter-thymeleaf (Template engine)
```

### Drivers
```xml
- mysql-connector-j (MySQL Driver)
```

### Tools
```xml
- lombok (Annotations)
- thymeleaf-extras-springsecurity6 (Security in views)
```

---

## 🎯 Bước Tiếp Theo

1. **Hoàn Thiện Tính Năng**
   - [ ] Tìm kiếm & lọc sân
   - [ ] Đặt sân & quản lý
   - [ ] Thanh toán

2. **Nâng Cao Bảo Mật**
   - [ ] JWT Tokens
   - [ ] OAuth2
   - [ ] Email verification

3. **Tối Ưu Hóa**
   - [ ] Caching
   - [ ] Search indexing
   - [ ] CDN images

4. **Monitoring**
   - [ ] Logging
   - [ ] Error tracking
   - [ ] Analytics

---

## 📞 Hỗ Trợ

Nếu gặp vấn đề, kiểm tra:
1. Application logs
2. Database connection
3. Java version (21+)
4. MySQL running

---

**Phiên Bản:** 0.0.1-SNAPSHOT
**Ngôn Ngữ:** Java 21 + Spring Boot 4.0.6
**Database:** MySQL 8.0+
**Cập Nhật:** 2026
