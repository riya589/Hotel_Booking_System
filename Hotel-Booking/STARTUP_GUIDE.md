# Hotel Booking System - Run Guide

## Prerequisites
- Java 17 or newer installed
- MySQL running on `localhost:3306`
- Maven wrapper is included as `mvnw.cmd`

## Database Setup
The project uses this default database config from `src/main/resources/application.properties`:
- Database: `hotel_db`
- Username: `root`
- Password: `Riya1611`

You can change them with environment variables:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

Example MySQL database creation if needed:
```sql
CREATE DATABASE hotel_db;
```

## Important Note
This project currently uses:
```properties
spring.jpa.hibernate.ddl-auto=create
```
This recreates the schema on startup so the upgraded demo project always starts with a clean working structure and seeded demo data.

## How To Start The Project
From the project root, run:
```powershell
.\mvnw.cmd spring-boot:run
```

## Exact Terminal Commands To Run One By One
If you have already set up the project before, use these exact commands in PowerShell:

```powershell
cd C:\Users\royri\Downloads\Hotel-Booking\Hotel-Booking
.\mvnw.cmd spring-boot:run
```

### Terminal Flow
1. Open PowerShell
2. Move into the project folder:
```powershell
cd C:\Users\royri\Downloads\Hotel-Booking\Hotel-Booking
```
3. Start the Spring Boot project:
```powershell
.\mvnw.cmd spring-boot:run
```
4. Wait until you see Spring Boot start successfully
5. Open the project in your browser at:
```text
http://localhost:8080/
```

### Main URLs After Startup
- Home page: `http://localhost:8080/`
- Hotels page: `http://localhost:8080/hotels`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Admin dashboard: `http://localhost:8080/admin/dashboard`
- My bookings: `http://localhost:8080/my-bookings`

## How To Build/Test
```powershell
.\mvnw.cmd test
```

## Application URLs
- Home page: `http://localhost:8080/`
- Hotel list: `http://localhost:8080/hotels`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Admin dashboard: `http://localhost:8080/admin/dashboard`
- My bookings: `http://localhost:8080/my-bookings`

## Demo Credentials
### Admin
- Email: `admin@hotel.com`
- Password: `admin123`

### Customer
- Email: `user@hotel.com`
- Password: `user123`

Extra demo users:
- `riya@hotel.com / user123`
- `demo@hotel.com / user123`

## Basic Demo Flow
1. Open `http://localhost:8080/`
2. Login with customer credentials
3. Go to `Hotels`
4. Open a hotel
5. Choose dates and create booking
6. Complete payment on booking confirmation page
7. Open invoice page
8. Check `My Bookings`
9. Login as admin and open `Admin Dashboard`

## Sample API Testing Flow
### 1. Login
`POST /auth/login`
```json
{
  "email": "user@hotel.com",
  "password": "user123"
}
```

### 2. Get hotels
`GET /api/hotels`

### 3. Create booking
`POST /api/bookings`
```json
{
  "roomId": 1,
  "checkInDate": "2026-05-01",
  "checkOutDate": "2026-05-03",
  "numberOfGuests": 1
}
```

### 4. Get invoice
`GET /api/bookings/{bookingId}/bill`

### 5. Pay
`POST /api/payments/{bookingId}`
```json
{
  "bookingId": 5,
  "amount": 11232.00,
  "paymentMethod": "UPI"
}
```

### 6. Admin analytics
`GET /api/admin/analytics`

## Notes For Viva / Presentation
- Authentication uses Spring Security + JWT
- Booking logic checks overlapping dates properly
- Billing is auto-generated after booking creation
- Payment is simulated but realistic for project demo
- Thymeleaf pages connect to backend APIs for full-stack flow



## Exact Terminal Commands To Run One By One
If you have already set up the project before, use these exact commands in PowerShell:

```powershell
cd C:\Users\royri\Downloads\Hotel-Booking\Hotel-Booking
.\mvnw.cmd spring-boot:run
```

### Terminal Flow
1. Open PowerShell
2. Move into the project folder:
```powershell
cd C:\Users\royri\Downloads\Hotel-Booking\Hotel-Booking
```
3. Start the Spring Boot project:
```powershell
.\mvnw.cmd spring-boot:run
```
4. Wait until you see Spring Boot start successfully
5. Open the project in your browser at:
```text
http://localhost:8080/
```