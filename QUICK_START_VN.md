# ⚡ Quick Start - Hệ Thống Đặt Sân Đá Bóng

## 🚀 Chạy Nhanh trong 3 Bước

### 1️⃣ Chuẩn Bị Database
```sql
-- Mở MySQL và chạy:
CREATE DATABASE datsanjava CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2️⃣ Chạy Ứng Dụng
```bash
# Từ thư mục project
mvn clean spring-boot:run

# Hoặc từ terminal
mvn clean package && java -jar target/datsan-0.0.1-SNAPSHOT.jar
```

### 3️⃣ Truy Cập Trang Web
```
🌐 Trang chính: http://localhost:8080
📝 Đăng ký: http://localhost:8080/register
🔑 Đăng nhập: http://localhost:8080/login
```

---

## 📝 Hướng Dẫn Sử Dụng

### ✍️ Đăng Ký Tài Khoản

1. Truy cập: http://localhost:8080/register
2. Điền thông tin:
   - **Họ và Tên**: Nhập tên đầy đủ
   - **Email**: example@gmail.com
   - **Số điện thoại**: (Tùy chọn)
   - **Tên đăng nhập**: user123 (độc lập)
   - **Mật khẩu**: Password123
   - **Xác nhận mật khẩu**: Password123
3. Bấm "Tạo Tài Khoản"
4. Chuyển sang trang đăng nhập

### 🔐 Đăng Nhập

1. Truy cập: http://localhost:8080/login
2. Nhập:
   - **Tên đăng nhập**: user123
   - **Mật khẩu**: Password123
3. Bấm "Đăng Nhập"
4. ✅ Vào Dashboard thành công!

### 📊 Dashboard

Sau khi đăng nhập, bạn sẽ thấy:
- 👋 Lời chào theo tên người dùng
- 📍 25+ sân bóng có sẵn
- 📅 Thống kê đặt sân của bạn
- 🎯 Các tính năng chính (sắp có)
- 🚪 Nút đăng xuất ở góc trên

---

## 🎨 Giao Diện

### 🔵 Trang Đăng Nhập
- Thiết kế xanh dương chuyên nghiệp
- Animation mượt mà
- Input field tương tác

### 🔴 Trang Đăng Ký
- Gradient hồng sinh động
- Form chi tiết với validation
- Hướng dẫn yêu cầu mật khẩu

### 🟣 Dashboard
- Thiết kế modern với gradient tím
- Responsive mobile-friendly
- Stats cards động
- Feature showcase

---

## ✨ Tính Năng Bảo Mật

✅ **BCrypt Password Encryption**
- Mật khẩu mã hóa an toàn
- Không thể giải mã

✅ **Spring Security**
- Xác thực người dùng
- Phân quyền vai trò (Customer/Staff/Admin)
- CSRF protection

✅ **Session Management**
- Tự động quản lý phiên
- Timeout xác định

---

## 🐛 Các Vấn Đề Thường Gặp

| Vấn Đề | Giải Pháp |
|--------|----------|
| **Port 8080 đã sử dụng** | Thay đổi `server.port=8081` trong application.properties |
| **MySQL không kết nối** | Kiểm tra MySQL đang chạy, user/pass đúng |
| **Trang không tải** | Xóa cache browser, F5 refresh, Ctrl+Shift+Del |
| **Lỗi đăng nhập** | Kiểm tra tên đăng nhập/mật khẩu đúng chưa |
| **Form đăng ký báo lỗi** | Kiểm tra mật khẩu xác nhận, email duy nhất |

---

## 📁 Tệp Quan Trọng

```
src/main/java/com/tanh/datsan/
├── controller/AuthController.java     ← Request handlers
├── service/AuthService.java           ← Business logic
├── security/UserDetailsServiceImpl.java ← Authentication
├── config/SecurityConfig.java         ← Spring Security config
├── repository/AccountRepository.java  ← Database queries
└── entity/Account.java                ← User model

src/main/resources/templates/
├── login.html                         ← Trang đăng nhập
├── register.html                      ← Trang đăng ký
└── dashboard.html                     ← Bảng điều khiển

application.properties                 ← Database config
```

---

## 🔄 Workflow Xác Thực

```
[Người Dùng] 
    ↓ (Đăng ký)
[Nhập Thông Tin] → [Validate] → [Hash Password] → [Lưu DB]
    ↓
[Đăng Nhập] → [Compare Password] → [Create Session] → [Dashboard]
    ↓
[Truy Cập Tính Năng] → [Check Permission] → [Hiển Thị/Từ Chối]
    ↓
[Đăng Xuất] → [Destroy Session] → [Về Trang Login]
```

---

## 🛠️ Cấu Hình Tùy Chỉnh

### Thay Đổi Port

File: `src/main/resources/application.properties`
```properties
# Mặc định 8080
server.port=8081
```

### Thay Đổi Database

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/database_moi
spring.datasource.username=new_user
spring.datasource.password=new_password
```

### Bật/Tắt SQL Logging

```properties
# Xem SQL queries
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

---

## 📚 Công Nghệ Sử Dụng

- **Backend**: Spring Boot 4.0.6, Java 21
- **Frontend**: Thymeleaf, HTML5, CSS3
- **Database**: MySQL 8.0
- **Security**: Spring Security, BCrypt
- **Build**: Maven

---

## ✅ Checklist Kiểm Tra

- [ ] Java 21+ được cài đặt
- [ ] MySQL đang chạy
- [ ] Maven được cài đặt
- [ ] Database `datsanjava` được tạo
- [ ] `application.properties` đúng
- [ ] Port 8080 không bị chiếm dụng
- [ ] Chạy thành công: `mvn clean spring-boot:run`
- [ ] Truy cập được: http://localhost:8080/login
- [ ] Đăng ký tài khoản mới
- [ ] Đăng nhập thành công
- [ ] Thấy Dashboard

---

## 🎯 Tiếp Theo?

Sau khi hoàn tất phần xác thực, bạn có thể:

1. **Thêm Tính Năng Sân Bóng**
   - Danh sách sân
   - Chi tiết sân
   - Bạn bản sân

2. **Triển Khai Đặt Sân**
   - Chọn sân & giờ
   - Xác nhận đặt
   - Lịch sử đặt

3. **Thanh Toán**
   - Integrate payment gateway
   - Lưu lịch sử thanh toán

4. **Nâng Cao Bảo Mật**
   - Email verification
   - 2FA
   - OAuth2

---

**🎉 Chúc bạn sử dụng vui vẻ!**

Nếu gặp vấn đề, kiểm tra file `README_VN.md` để có hướng dẫn chi tiết hơn.
