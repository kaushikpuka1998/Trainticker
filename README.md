# 🚆 TrainTicker - IRCTC Like Train Booking System

TrainTicker is a scalable backend system inspired by IRCTC, built using Java, Spring Boot, PostgreSQL, and JPA/Hibernate.
The system supports train management, route management, coach & seat generation, train schedules, and seat availability search.

---

# ✨ Features

## 🚉 Train Management

* Create trains with:

  * Train Number
  * Train Name
  * Route Stations
  * Coaches
  * Running Schedule

## 🛤 Route Management

* Add multiple stations for a train
* Store:

  * Arrival Time
  * Departure Time
  * Distance From Source
  * Day Number
  * Station Order

## 🏙 Station Reusability

* Stations are normalized and reused across trains
* Avoid duplicate station creation using station code lookup

## 🚆 Coach Management

Supports multiple coach types:

* 1A
* 2A
* 3A
* SL
* Pantry

## 💺 Automatic Seat Generation

Seats are auto-generated based on coach type.

Example:

| Coach Type | Seat Count |
| ---------- | ---------- |
| 1A         | 24         |
| 2A         | 54         |
| 3A         | 72         |
| SL         | 80         |

## 📅 Train Scheduling

Supports:

* Daily trains
* Weekday trains
* Weekend trains
* Custom running days

Example:

```text
1111111 -> Daily
1111100 -> Monday-Friday
0000011 -> Weekend
1010100 -> Monday, Wednesday, Friday
```

## 🔍 Availability Search (Planned)

API design supports:

* Source Station
* Destination Station
* Journey Date
* Class Type
* Dynamic Seat Availability

---

# 🏗 Tech Stack

| Technology      | Usage                 |
| --------------- | --------------------- |
| Java 21         | Backend               |
| Spring Boot     | REST APIs             |
| Spring Data JPA | ORM                   |
| Hibernate       | Persistence           |
| PostgreSQL      | Database              |
| Lombok          | Boilerplate Reduction |
| Maven           | Build Tool            |

---

# 📂 Project Structure

```text
src/main/java/com/kgstrivers/trainticker
│
├── Controllers
├── Services
├── Repositories
├── Entities
├── DTOs
├── Config
└── Exceptions
```

---

# 🧩 Entity Relationships

## Train

* One Train → Many RouteStations
* One Train → Many Coaches
* One Train → Many Schedules

## Coach

* One Coach → Many Seats

## RouteStation

* Many RouteStations → One Station

---

# 🗃 Database Design

## Core Tables

* trains
* stations
* route_stations
* coaches
* seats
* train_schedules
* bookings

---

# 🚀 Sample Train Creation API

## POST `/api/v1/trains`

### Request Body

```json
{
  "trainNumber": "12301",
  "trainName": "Howrah New Delhi Rajdhani Express",

  "schedules": [
    {
      "startDate": "2026-01-01",
      "endDate": "2026-12-31",
      "runningDays": "1111111",
      "active": true
    }
  ],

  "routeStations": [
    {
      "code": "HWH",
      "name": "Howrah Junction",
      "state": "West Bengal",
      "stationOrder": 1,
      "arrivalTime": null,
      "distanceFromSource": 0,
      "departureTime": "16:55",
      "dayNumber": 1
    },
    {
      "code": "NDLS",
      "name": "New Delhi",
      "state": "Delhi",
      "stationOrder": 2,
      "arrivalTime": "10:00",
      "distanceFromSource": 1447,
      "departureTime": null,
      "dayNumber": 2
    }
  ],

  "coaches": [
    {
      "coachNumber": "A1",
      "coachType": "2A"
    },
    {
      "coachNumber": "B1",
      "coachType": "3A"
    }
  ]
}
```

---

# ⚙️ How To Run

## Clone Repository

```bash
git clone <your-github-url>
```

## Configure PostgreSQL

Update `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/trainticker
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
```

## Run Application

```bash
mvn spring-boot:run
```

---

# 📌 Upcoming Features

* Ticket Booking API
* Waiting List
* RAC
* Dynamic Seat Allocation
* Fare Calculation
* Tatkal Booking
* User Authentication
* Payment Integration
* Kafka Based Booking Queue
* Redis Caching
* Distributed Locking
* Real-time Seat Availability

---

# 🧠 Design Concepts Used

* Entity Relationships
* JPA Cascade Operations
* Bidirectional Mapping
* DTO Design
* Database Normalization
* Automatic Seat Generation
* Schedule Management
* Route Ordering
* Train Availability Logic

---

# 📖 Learning Outcomes

This project demonstrates:

* Scalable backend architecture
* Real-world railway domain modeling
* Spring Boot best practices
* Complex JPA relationship handling
* Production-grade API design

---

# 👨‍💻 Author

Kaushik Ghosh

Software Engineer | Backend Developer | Java & Spring Boot Enthusiast
