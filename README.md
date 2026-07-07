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

<img width="917" height="482" alt="image" src="https://github.com/user-attachments/assets/8fa394ba-efd0-48ff-b65f-7e1c5e241db5" />

<img width="913" height="490" alt="image" src="https://github.com/user-attachments/assets/7899827e-1992-443b-b1d9-63743882cdf0" />

<img width="920" height="327" alt="image" src="https://github.com/user-attachments/assets/681f47b0-c674-4d5c-8b76-df6129489387" />

<img width="922" height="208" alt="image" src="https://github.com/user-attachments/assets/192bc2d8-fc64-4737-bc0d-739660a5b9a8" />

<img width="912" height="407" alt="image" src="https://github.com/user-attachments/assets/384c497f-2a08-4960-95cd-3d96adc47ec0" />

<img width="907" height="202" alt="image" src="https://github.com/user-attachments/assets/92f0dfb4-a41f-4b8c-9086-54f9a2f19625" />


---   

# 📸 Application Preview

## 🏠 Home Page

<img width="1886" height="903" alt="image" src="https://github.com/user-attachments/assets/3d80b618-1f4b-4d80-8244-6c3be8b7e79e" />

Browse upcoming events with smart search and filtering.

---

## 🔑 Login Page

<img width="945" height="895" alt="image" src="https://github.com/user-attachments/assets/39a8dee7-074d-453e-9be5-8cfb3771b006" />

Secure authentication using JWT.

---

## 📝 Register Page

<img width="723" height="912" alt="image" src="https://github.com/user-attachments/assets/3e3bc8d2-f04f-40f5-a130-0c4bab3e40b5" />

Create a new user account.

---

## 🎟️ Events Page

<img width="1786" height="562" alt="image" src="https://github.com/user-attachments/assets/f6f4a63f-86f9-4ee0-a84e-9b5a321a5668" />

Browse all available events with search and filters.

---

## 📄 Event Details

<img width="1886" height="908" alt="image" src="https://github.com/user-attachments/assets/4a193f47-1714-4d50-8a44-870da8eeea9c" />

View event information, venue, timings, and available seats.

---

## 🪑 Seat Booking

<img width="1882" height="910" alt="image" src="https://github.com/user-attachments/assets/964f9bb5-5d97-48f5-868d-528a8ad75976" />

Interactive seat selection with real-time availability.

---

## 💳 Payment Page

<img width="1902" height="905" alt="image" src="https://github.com/user-attachments/assets/e4cfeda2-b3fd-4ac4-9bbd-e5848331bb15" />

<img width="1887" height="902" alt="image" src="https://github.com/user-attachments/assets/2150aa70-77c9-43fd-816e-f0521e8e7dd9" />

Complete the booking process securely.

---

## 👤 User Dashboard

<img width="1901" height="902" alt="image" src="https://github.com/user-attachments/assets/1285a2bc-7cf5-43f2-8272-2c4d322c9fc5" />

View booking history and manage profile.

---

## 🎯 Organizer Dashboard

<img width="1903" height="903" alt="image" src="https://github.com/user-attachments/assets/a10cfd53-3299-421c-8bac-d84c9cd11c97" />

Create, update, and manage events.

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
