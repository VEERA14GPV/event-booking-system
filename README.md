# 🎉 EventSphere – Smart Event Booking Platform

EventSphere is a full-stack Event Booking Platform that enables users to discover events, reserve seats in real-time, and securely complete payments. It provides separate dashboards for users, organizers, and administrators while ensuring seamless booking with real-time seat locking and live updates.

---

# 🚀 Features

## 🎟️ Event Management
- Create, edit, and delete events
- Organize multiple shows for each event
- Categorize events by type
- Upload event details and schedules

## 🪑 Real-Time Seat Booking
- Interactive seat selection
- Live seat availability updates
- Temporary seat locking to prevent double booking
- Booking confirmation with seat allocation

## 🔍 Smart Search
- Search events by name
- Filter by category
- Browse upcoming events
- Fast event indexing for improved search

## 💳 Secure Payments
- Razorpay payment integration
- Payment verification
- Booking confirmation after successful payment
- Transaction history

## 👥 User Authentication
- User Registration & Login
- JWT Authentication
- Role-Based Authorization
- Organizer & Admin access control

## 📡 Live Updates
- WebSocket-based real-time seat updates
- Instant booking status synchronization
- Live availability refresh without page reload

## 📊 Dashboards
- User Dashboard
- Organizer Dashboard
- Admin Dashboard
- Booking History
- Event Analytics

---

# 🖥️ UI Overview

- 🟢 Available Seats
- 🔴 Booked Seats
- 🟡 Locked Seats
- 🎫 Event Cards
- 📅 Show Listings
- 👤 User Dashboard
- ⚙️ Admin Dashboard

---

# 🧠 Core Functionalities

### 1. Event Management
Allows organizers to create, update, and manage events along with multiple show timings.

### 2. Seat Reservation
Users can select seats interactively while the system temporarily locks selected seats to avoid duplicate bookings.

### 3. Authentication & Authorization
JWT authentication with role-based access ensures secure access for Users, Organizers, and Admins.

### 4. Real-Time Communication
WebSockets instantly update seat availability across all connected users.

---

# 🛠️ Technologies Used

## Backend
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT Authentication
- WebSockets (STOMP)
- Redis
- Elasticsearch
- Maven

## Frontend
- React.js
- Vite
- Tailwind CSS
- Axios
- React Router

## Database
- MySQL

## Other Tools
- Docker
- Docker Compose
- Swagger API Documentation

---

# ⚙️ Project Structure

```
EventSphere/
│
├── backend/
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   ├── service/
│   ├── security/
│   ├── websocket/
│   ├── payment/
│   └── EventBookingSystemApplication.java
│
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── auth/
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── layouts/
│   │   ├── pages/
│   │   ├── utils/
│   │   └── websocket/
│   └── package.json
│
├── docker-compose.yml
└── README.md
```

---

# ▶️ Getting Started

## 1. Clone Repository

```bash
git clone https://github.com/VEERA14GPV/EventSphere.git
cd EventSphere
```

---

## 2. Configure Environment Variables

Create a `.env` file using the provided `.env.example`.

Configure:

- MySQL Database
- JWT Secret
- Razorpay Keys
- Redis
- Elasticsearch

---

## 3. Run Backend

```bash
./mvnw spring-boot:run
```

or

```bash
mvn spring-boot:run
```

---

## 4. Run Frontend

```bash
cd frontend

npm install

npm run dev
```

---

## 5. Run with Docker (Optional)

```bash
docker-compose up --build
```

---

# 📊 How It Works

1. Register or Login
2. Browse Available Events
3. Select Event & Show
4. Choose Seats
5. Seats are Locked Temporarily
6. Payment
7. Booking Confirmed
8. Receive Booking Details

---

# 🔐 Security Features

- JWT Authentication
- Role-Based Access Control
- Secure Password Encryption
- Seat Locking using Redis
- Distributed Locking
- Global Exception Handling

---

# 📡 API Features

- Authentication APIs
- Event APIs
- Booking APIs
- Seat APIs
- Payment APIs
- Search APIs
- WebSocket Notifications
- Swagger Documentation

---

# 📷 Swagger API Documentation



---   

# 🎯 Learning Outcomes

- Full Stack Web Development
- Spring Boot REST APIs
- React Frontend Development
- JWT Authentication
- WebSocket Communication
- Redis Caching & Distributed Locking
- Elasticsearch Integration
- Docker Deployment
- Real-Time Booking Systems

---

# 📌 Future Improvements

- 🎯 AI Event Recommendation System
- 📱 Mobile Application
- 🌍 Multi-language Support
- 📍 Google Maps Venue Integration
- 📧 Email & SMS Notifications
- 🎟️ QR Code Based Ticket Validation
- ☁️ Cloud Deployment (AWS/Azure)
- 📈 Advanced Analytics Dashboard

---

# 🤝 Contributing

Contributions are welcome.

Feel free to:

- Fork the repository
- Create feature branches
- Report issues
- Submit Pull Requests

---

# 📸 Application Preview

### Home Page
Browse upcoming events with smart search and filtering.

### Event Details
View event information, schedules, and available shows.

### Seat Booking
Interactive seat map with real-time availability.

### Organizer Dashboard
Manage events, shows, and bookings.

### Admin Dashboard
Monitor users, organizers, and platform activities.

---

# 👨‍💻 Author

Veera (VEERA14GPV) 
GitHub: https://github.com/VEERA14GPV
