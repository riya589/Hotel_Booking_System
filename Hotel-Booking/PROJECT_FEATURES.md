# HotelHaven - Project Features and Report Outline

*This document outlines all major architectural decisions, business logic components, and UI/UX features built into the **Hotel Booking and Management System**. It is designed to act as the primary structural reference for a college/university project report.*

---

## 1. Introduction and Objectives
The Hotel Booking system was developed to simulate a real-world, full-stack enterprise web application (like MakeMyTrip, Agoda, or OYO). The objective was to create a robust backend engine that handles concurrency (double-booking prevention), secure authentication, and invoice generation, smoothly connected to a highly responsive, modern frontend layer.

## 2. Technical Stack
- **Backend Framework**: Java 17, Spring Boot 3
- **Database**: MySQL mapping via Spring Data JPA / Hibernate
- **Security**: Spring Security integrated with stateless JWT (JSON Web Tokens)
- **Frontend Template Engine**: Server-side Thymeleaf rendering
- **Styling and UI**: HTML5, Vanilla JavaScript, Vanilla CSS, and Bootstrap 5
- **Build System**: Maven Wrapper

---

## 3. Core Modules and Business Logic

### A. Dynamic Availability and Booking Engine
The core of the application lies in its capacity to prevent reservation conflicts effortlessly.
- **Date-Overlap Checking**: When a user selects a date range, the database executes SQL logic checking all existing bookings for that exact room. The room is only presented if `requested check-in >= existing check-out` OR `requested check-out <= existing check-in`.
- **Real-Time Restraints**: The frontend date calendars (`<input type="date">`) dynamically calculate the local timezone and permanently disable all dates prior to the current real-time "Today."
- **Capacity Gates**: A user cannot artificially inject more guests than the room physically enables. A `SINGLE` room enforces 1 guest, while a `SUITE` accommodates up to 4. 

### B. Scalable Database Seeding
Unlike standard college projects with hardcoded limited data, this backend actively generates a massive, realistic database at run-time (`DataLoader.java`). 
- **Voluminous Generation**: Upon application startup, the generator maps out 11 distinct Indian states and roughly 35+ prime cities (from Manali to Munnar). 
- **Algorithm**: The algorithm loops through each city dynamically spinning up between **2 to 5 distinct mock properties** uniquely named with varied pricing structures, totaling 50+ fully functioning entities instantly upon deployment.

### C. Advanced Tiered Cancellations
Instead of simply dropping a row from a database, the pipeline handles financial responsibilities smoothly using a custom-engineered Bootstrap UI Modal and `api/bookings/{id}/cancel` route:
- **Terms Overlay**: If a user taps "Cancel", an elegant window intercepts the mouse click enforcing terms and conditions before API execution.
- **Dynamic Refund Tiers**:
  - Cancel before 2 days: 90% Cashback
  - Cancel 1 day before: 70% Cashback
  - Cancel within 12 hours: 40% Cashback 

### D. Authentication and Security
- **JWT Architecture**: The application uses tokenized request headers instead of slow web sessions. This means the frontend can ping secure `/api/admin/*` paths lightning fast.
- **Role Isolation**:
  - `ROLE_CUSTOMER`: Permitted to browse catalogs, generate bookings, request invoices, and execute payments.
  - `ROLE_ADMIN`: Allowed inside the heavily fortified "Admin Dashboard" where analytics sit, bypassing normal browsing patterns.
- **Deep Redirection UX**: If an un-authenticated guest taps "Book Now", the Javascript intercepts the HTTP path, passes it as a `?next=` parameter to the `/login` view, and gracefully returns the user specifically back to that exact room they initially wanted!
- **Data Protection**: `BCrypt` hashing secures all user passwords at the data layer. 

---

## 4. UI/UX Excellence (Frontend Highlights)
The interface was crafted exclusively to mirror elite real-world architectures. 

- **Smart "MakeMyTrip-Style" Location Dropdown**: The raw search inputs actively query a mapped Array dictionary containing the 35+ destinations. As the user types, a dynamic floating box physically manifests and surgically filters down matching States and Cities cleanly on the DOM.
- **Dynamic Chip Rendering**: Raw text strings passed by the database like `"Free Breakfast, Buffet, AC, Bathtub"` are processed through Javascript string-splitting loops and wrapped inside beautifully stylized pill chips (tags) underneath the main Hotel Headers. 
- **Simulated Real-World Reviews**: The application simulates massive community traffic by programmatically associating fake, structured guest reviews attached reliably to Hotel IDs. Every single hotel manifests specific, generated reviews praising amenities. 

---

## 5. Domain Models (Database Schema)

1. **User Table**: Captures `{id, fullName, email, phone, encrypted_password, role, created_at}`
2. **Hotel Table**: Captures `{id, name, location, address, description, amenities (TEXT), imageUrl, rating, created_at}`
3. **Room Table**: Captures `{id, roomNumber, roomType [ENUM], pricePerNight, capacity, available (boolean), hotel_id (Foreign Key)}`
4. **Booking Table**: Captures `{id, checkInDate, checkOutDate, numberOfGuests, totalNights, totalPrice, paymentStatus, bookingStatus, user_id, room_id, hotel_id}`
5. **Bill/Invoice Table**: Captures `{id, baseAmount, taxAmount, serviceCharge, finalAmount, generated_at, booking_id}`
6. **Payment Table**: Captures `{id, amount, paymentMethod, paymentStatus, transactionId, payment_date, booking_id}`

---

## 6. Real-World Demonstrations included in the Build
For the final review presentations, the build automatically compiles:
1. `admin@hotel.com` | `admin123` (Administrative Layer Demo)
2. `user@hotel.com`  | `user123` (Generic Target User Demo) 
3. Fully linked Invoice generation logic, where completing a simulated payment switches backend states instantly without manual overrides.
