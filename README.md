# 🎟️ Event Booking System

A production-ready Event Booking System built using Spring Boot that enables secure event management, show scheduling, seat booking, payment processing, real-time seat updates, distributed locking, caching, and enterprise-grade API security mechanisms.

The system is designed to handle concurrent booking requests efficiently while preventing double bookings through Redis-based distributed locking and providing real-time seat availability updates using WebSockets.

---

# 🚀 Features

## 🔐 Authentication & Authorization

* User Registration
* User Login
* JWT-Based Authentication
* Role-Based Access Control (RBAC)
* Protected REST APIs

---

## 🎭 Event Management

* Create Events
* Update Events
* View Events
* Event Filtering using JPA Specifications
* Event Ownership Validation

---

## 🎬 Show Management

* Create Shows
* Manage Show Schedules
* View Show Information
* Associate Shows with Events

---

## 💺 Seat Management

* Seat Availability Tracking
* Seat Status Management
* Real-Time Seat Updates
* Seat Lock Validation

---

## 📦 Booking Management

* Create Bookings
* Booking Validation
* Booking Status Tracking
* Expired Booking Cleanup

---

## 🔒 Distributed Seat Locking

To prevent multiple users from booking the same seat simultaneously:

* Redis-Based Distributed Locks
* Temporary Seat Reservation
* Lock Expiration Handling
* Double Booking Prevention

---

## ⚡ Real-Time Updates

The application uses WebSockets and STOMP messaging for live updates.

### Supported Features

* Live Seat Availability Updates
* Seat Lock Notifications
* Real-Time Booking Updates

---

## 💳 Payment Integration

Integrated with Razorpay for secure payment processing.

### Features

* Payment Initiation
* Payment Verification
* Payment Status Tracking

---

## 🚀 Performance & Reliability

* Redis Caching
* Distributed Lock Management
* Scheduled Cleanup Jobs
* Optimized Database Access

---

## 🛡️ API Security

### JWT Authentication

* Secure Token-Based Authentication
* Protected Endpoints

### Rate Limiting

* Prevents API Abuse
* User-Based Request Throttling

### Idempotency Protection

* Prevents Duplicate Requests
* Ensures Safe Payment Operations
* Protects Booking Transactions

### Exception Handling

* Centralized Global Exception Handling
* Standardized Error Responses

---

## 📖 API Documentation

* Swagger Integration
* OpenAPI Documentation Support

---

# 🛠️ Technologies Used

## Backend

* Java 17
* Spring Boot 3.5.4
* Spring Security
* Spring Data JPA
* Hibernate

## Database

* MySQL

## Caching & Distributed Systems

* Redis
* Redis Distributed Locking

## Real-Time Communication

* WebSocket
* STOMP Messaging

## Security

* JWT Authentication
* Rate Limiting
* Idempotency Protection

## Payment Gateway

* Razorpay

## Documentation & Build Tools

* Swagger / OpenAPI
* Maven

---

# ⚙️ Project Structure

```text
src/main/java/com/booking

├── config/
├── controller/
├── dto/
├── entity/
├── enums/
├── exception/
├── locking/
├── payment/
├── repository/
├── scheduler/
├── security/
│   ├── idempotency/
│   └── ratelimit/
├── service/
│   ├── authorization/
│   ├── cache/
│   └── websocket/
├── specification/
└── util/
```

---

# 🔄 Real-Time Communication

## WebSocket Endpoint

```text
/ws
```

## Message Broker

```text
/topic/**
```

## Application Destination Prefix

```text
/app
```

### Used For

* Seat Lock Updates
* Seat Availability Changes
* Booking Notifications

---

# 📦 Booking Workflow

```text
User Login
      ↓
Browse Events
      ↓
Select Show
      ↓
Choose Seats
      ↓
Redis Seat Lock
      ↓
Razorpay Payment
      ↓
Booking Confirmation
      ↓
WebSocket Notification
```

---

# 🔒 Security Features

## Authentication

* JWT-Based Authentication
* Secure API Access

## Authorization

* Role-Based Access Control

## Request Protection

* Rate Limiting
* Idempotency Validation

## Error Handling

* Centralized Exception Management

---

# 📖 API Documentation

After starting the application, access Swagger UI from the configured Swagger endpoint.

Swagger provides:

* API Testing
* Request/Response Documentation
* Endpoint Exploration

---

# ▶️ Getting Started

## 1. Clone Repository

```bash
git clone https://github.com/VEERA14GPV/event-booking-system.git
cd event-booking-system
```

---

## 2. Configure MySQL

Update the database configuration in:

```properties
src/main/resources/application.properties
```

Configure:

```properties
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
```

---

## 3. Configure Redis

Ensure Redis is running and update Redis connection properties if required.

---

## 4. Configure Razorpay

Update Razorpay credentials in:

```properties
application.properties
```

---

## 5. Build Project

```bash
mvn clean install
```

---

## 6. Run Application

```bash
mvn spring-boot:run
```

---

# 🎯 Learning Outcomes

This project helped in understanding:

* Spring Boot Application Development
* REST API Design
* JWT Authentication & Authorization
* Role-Based Access Control
* Redis Caching
* Redis Distributed Locking
* WebSocket Real-Time Communication
* Payment Gateway Integration
* Rate Limiting Strategies
* Idempotency Handling
* JPA Specifications
* Enterprise Security Patterns
* Exception Handling Best Practices

---

# 📌 Future Enhancements

* Email Notifications
* Booking Cancellation & Refund Workflow
* User Booking History Dashboard
* Admin Analytics Dashboard
* QR Code Ticket Generation
* Kubernetes Deployment Support
* CI/CD Pipeline Integration
* Cloud Deployment (AWS/Azure/GCP)

---

# 👨‍💻 Author

**Veera (VEERA14GPV)**

GitHub:
https://github.com/VEERA14GPV
