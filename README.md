# 🚆 TrainTicker - IRCTC Like Train Booking System

TrainTicker is a scalable backend railway reservation system inspired by IRCTC, built using Java, Spring Boot, PostgreSQL, and JPA/Hibernate.

The project supports:

* Train Management
* Route Management
* Coach & Seat Generation
* Train Scheduling
* Passenger Booking
* Dynamic Seat Allocation
* Availability Search

---

# ✨ Features

# 🚉 Train Management

* Create trains with:

  * Train Number
  * Train Name
  * Running Schedules
  * Route Stations
  * Coaches

---

# 🛤 Route Management

Supports:

* Multi-day journeys
* Station ordering
* Distance tracking
* Arrival/Departure timings

Each route station stores:

* Station Code
* Station Name
* State
* Day Number
* Distance From Source
* Station Order

---

# 🏙 Station Reusability

Stations are normalized and reused across trains.

Example:

```text id="g2j98x"
HWH
NDLS
CSMT
```

exist only once in database and multiple trains reference them.

---

# 📅 Train Scheduling

Supports:

* Daily trains
* Weekday trains
* Weekend trains
* Custom schedules

Example:

```text id="7l8r5u"
1111111 -> Daily
1111100 -> Monday-Friday
0000011 -> Weekend
1010100 -> Monday, Wednesday, Friday
```

---

# 🚆 Coach Management

Supported coach types:

* 1A
* 2A
* 3A
* SL
* Pantry

---

# 💺 Automatic Seat Generation

Seats are automatically generated based on coach type.

Example:

| Coach Type | Seat Count |
| ---------- | ---------- |
| 1A         | 24         |
| 2A         | 54         |
| 3A         | 72         |
| SL         | 80         |

---

# 🎟 Booking System

Supports:

* Multi-passenger booking
* PNR generation
* Seat allocation
* Passenger mapping
* Journey segment handling

---

# 👤 Passenger Management

Each booking can contain multiple passengers.

Passenger details:

* Name
* Age
* Gender

Each passenger gets:

* Individual seat
* Individual booking status

---

# 🪑 Seat Allocation System

Seat allocation is based on:

* Journey date
* Class type
* Existing bookings
* Seat availability

Supported statuses:

* CONFIRMED
* RAC (Planned)
* WAITING (Planned)

---

# 🧠 Journey Segment Masking

TrainTicker uses segment-based booking logic similar to real railway systems.

Example route:

```text id="hdh6v5"
HWH -> ASN -> GAYA -> NDLS
```

If seat booked:

```text id="jlwm20"
HWH -> GAYA
```

Same seat can still be allocated:

```text id="jlwm21"
GAYA -> NDLS
```

This improves seat utilization significantly.

---

# 🔍 Availability Search (Planned)

API Design:

```http id="jlwm22"
GET /api/v1/availability
```

Supports:

* Source station
* Destination station
* Journey date
* Class type
* Dynamic seat availability

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

```text id="jlwm23"
src/main/java/com/kgstrivers/trainticker
│
├── Controllers
├── Services
├── Repositories
├── Entities
├── DAO
├── DTO
├── Config
└── Exceptions
```

---

# 🧩 Entity Relationships

# Train

* One Train → Many RouteStations
* One Train → Many Coaches
* One Train → Many Schedules
* One Train → Many Bookings

# Coach

* One Coach → Many Seats

# Booking

* One Booking → Many Passengers
* One Booking → Many BookedSeats

# Passenger

* One Passenger → One Allocated Seat

# RouteStation

* Many RouteStations → One Station

---

# 🗃 Database Tables

Core Tables:

* trains
* stations
* route_stations
* train_schedules
* coaches
* seats
* bookings
* passengers
* booked_seats

---

# 🚀 Sample Train Creation API

## POST `/api/v1/trains`

### Request Body

```json id="jlwm24"
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

# 🎟 Sample Booking API

## POST `/api/v1/bookings`

### Request Body

```json id="jlwm25"
{
  "trainNumber": "12301",
  "journeyDate": "2026-06-15",

  "sourceStationCode": "HWH",
  "destinationStationCode": "NDLS",

  "classType": "3A",

  "passengers": [
    {
      "name": "Kaushik Ghosh",
      "age": 26,
      "gender": "MALE"
    },
    {
      "name": "Rahul Sharma",
      "age": 30,
      "gender": "MALE"
    }
  ]
}
```

---

# 📤 Sample Booking Response

```json id="jlwm26"
{
  "pnr": "8745632198",
  "trainNumber": "12301",
  "trainName": "Howrah New Delhi Rajdhani Express",
  "journeyDate": "2026-06-15",
  "source": "HWH",
  "destination": "NDLS",
  "bookingStatus": "CONFIRMED",

  "passengers": [
    {
      "passengerName": "Kaushik Ghosh",
      "coachNumber": "B1",
      "seatNumber": "12",
      "bookingStatus": "CONFIRMED"
    },
    {
      "passengerName": "Rahul Sharma",
      "coachNumber": "B1",
      "seatNumber": "13",
      "bookingStatus": "CONFIRMED"
    }
  ]
}
```

---

# ⚙️ How To Run

# Clone Repository

```bash id="jlwm27"
git clone <your-github-url>
```

---

# Configure PostgreSQL

Update:

```properties id="jlwm28"
application.properties
```

```properties id="jlwm29"
spring.datasource.url=jdbc:postgresql://localhost:5432/trainticker

spring.datasource.username=postgres

spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
```

---

# Run Application

```bash id="jlwm30"
mvn spring-boot:run
```

---

# 📌 Upcoming Features

* RAC
* Waiting List
* Tatkal Booking
* Dynamic Pricing
* Seat Preference
* Lower/Middle/Upper Berth
* Fare Calculation
* User Authentication
* JWT Security
* Redis Seat Locking
* Kafka Booking Queue
* Distributed Locking
* Cancellation & Refund
* Email/SMS Notification
* Payment Integration

---

# 🧠 Design Concepts Used

* Database Normalization
* Entity Relationships
* JPA Cascade Operations
* Bidirectional Mapping
* Dynamic Seat Allocation
* Segment-Based Reservation
* Schedule Management
* Route Ordering
* Availability Algorithms
* Production-style Architecture

---

# 📖 Learning Outcomes

This project demonstrates:

* Scalable backend architecture
* Real-world railway reservation modeling
* Spring Boot best practices
* Advanced JPA relationship handling
* Production-grade API design
* Complex booking workflows

---

# 👨‍💻 Author

Kaushik Ghosh

Software Engineer | Backend Developer | Java & Spring Boot Enthusiast
