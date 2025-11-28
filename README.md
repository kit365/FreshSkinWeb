# 🌿 FreshSkin

> Hệ thống E-commerce Mỹ phẩm & Đề xuất Skincare thông minh

Nền tảng thương mại điện tử chuyên biệt về mỹ phẩm, được xây dựng với **Spring Boot** và **Next.js**. Hệ thống giải quyết bài toán "chọn mỹ phẩm phù hợp" thông qua tính năng **Skin Quiz** và thuật toán đề xuất sản phẩm cá nhân hóa, tuân thủ kiến trúc **Layered Architecture** hiện đại.

---

## 🚀 Công nghệ Sử dụng

### Backend Framework
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?logo=spring-boot)
![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![Maven](https://img.shields.io/badge/Maven-3.x-red?logo=apache-maven)

### Frontend Framework
![Next.js](https://img.shields.io/badge/Next.js-14.1.0-black?logo=next.js)
![TypeScript](https://img.shields.io/badge/TypeScript-5.x-blue?logo=typescript)
![Tailwind CSS](https://img.shields.io/badge/Tailwind-CSS-38B2AC?logo=tailwind-css)

### Cơ sở Dữ liệu (Hybrid)
![MySQL](https://img.shields.io/badge/MySQL-8.0-00618a?style=flat&logo=mysql&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-7.0-47A248?style=flat&logo=mongodb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7.0-red?style=flat&logo=redis)

### Bảo mật & Thanh toán
![Spring Security](https://img.shields.io/badge/Spring%20Security-Latest-brightgreen?logo=spring)
![JWT](https://img.shields.io/badge/JWT-Stateless-red?logo=json-web-tokens)
![VNPay](https://img.shields.io/badge/Payment-VNPay-blue)

### DevOps & Công cụ
![Docker](https://img.shields.io/badge/Docker-Compose-blue?logo=docker)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?logo=swagger)
![Lombok](https://img.shields.io/badge/Lombok-1.18.30-pink?logo=lombok)

---

## 🏗️ Kiến trúc

Dự án tuân theo mô hình **Layered Architecture** kết hợp với chiến lược **Hybrid Database**, tách biệt rõ ràng giữa logic nghiệp vụ, giao diện và lớp lưu trữ dữ liệu.

## ✨ Tính năng

- 🛍️ **E-commerce Toàn diện**
  - Tìm kiếm, lọc sản phẩm đa tiêu chí.
  - Giỏ hàng (Redis Session) và quản lý đơn hàng.
  - Thanh toán online qua cổng VNPay.

- 🧬 **Skin Intelligence (Đề xuất thông minh)**
  - **Skin Quiz**: Trắc nghiệm xác định loại da và vấn đề da.
  - **Auto Recommendation**: Gợi ý Routine chăm sóc da dựa trên kết quả phân tích.

- 🔐 **Bảo mật & Hiệu năng**
  - Xác thực JWT (Access + Refresh Token).
  - Caching danh mục và sản phẩm với Redis.

---
