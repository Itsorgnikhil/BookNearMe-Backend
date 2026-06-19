# 🏨 BookNearMe - Hotel Booking Platform (Backend)

A modern, scalable **Spring Boot 3.4** backend for a full-featured hotel booking platform with AI-powered features, dynamic pricing, and seamless payment integration.

---

## 🎯 Project Overview

**BookNearMe** is an hotel booking system built with:
- **Spring Boot 3.4** + Java 21
- **PostgreSQL 18** with pgvector for AI embeddings
- **JWT Authentication** with refresh token rotation
- **Spring AI** integration (Google Gemini)
- **Stripe Payment Processing**
- **Dynamic Pricing Engine** with multiple strategies
- **Role-Based Access Control** (Guest & Hotel Manager)

---

## 🚀 Quick Start

### Prerequisites

- **Java 21+** (download from [oracle.com](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.9+** (download from [maven.apache.org](https://maven.apache.org/))
- **PostgreSQL 18+** (download from [postgresql.org](https://www.postgresql.org/))
- **Git**

### Local Development Setup

#### Step 1: Clone Repository
```bash
git clone https://github.com/YOUR_USERNAME/BookNearMe-backend.git
cd BookNearMe-backend
```

#### Step 2: Database Setup

Create PostgreSQL database:
```sql
CREATE DATABASE "booknearme_db";

-- Connect to booknearme_db database
\c booknearme_db

-- Enable pgvector extension for AI embeddings
CREATE EXTENSION IF NOT EXISTS vector;

-- Verify extension
SELECT * FROM pg_extension WHERE extname = 'vector';
```

#### Step 3: Configure Environment

Create `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/booknearme_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Server
server.servlet.context-path=/api/v1
server.port=8080

# JWT Configuration
jwt.secretKey=a8sd6faihdfjkas2398789afhdskjasdfh87yhk3jha987sdf89aabsdf876 (fake key)

# Frontend URL (for CORS)
frontend.url=http://localhost:3000

# Stripe Payment Integration
stripe.secret.key=sk_test_YOUR_STRIPE_KEY
stripe.webhook.secret=whsec_YOUR_WEBHOOK_SECRET

# Google Gemini AI (Spring AI)
spring.ai.google.genai.api-key=YOUR_GEMINI_API_KEY
spring.ai.google.genai.chat.options.model=gemini-2.5-flash
spring.ai.google.genai.embedding.options.model=gemini-embedding-001

# PgVector Configuration
spring.ai.vectorstore.pgvector.enabled=true
spring.ai.vectorstore.pgvector.initialize-schema=true
spring.ai.vectorstore.pgvector.dimensions=768
spring.ai.vectorstore.pgvector.distance-type=COSINE_DISTANCE
```

#### Step 4: Install Dependencies & Run

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

App will start at: `http://localhost:8080/api/v1`

---

## 📚 API Documentation

### Swagger/OpenAPI

Once running, visit:
```
http://localhost:8080/api/v1/swagger-ui/index.html
```

---

## 🔐 Authentication

### JWT Flow

1. **Login/Signup** → Returns `accessToken`
2. **Access Token** stored in `localStorage`
3. **Every Request** → `Authorization: Bearer <accessToken>` header
4. **Token Expires** → 10 minutes
5. **Refresh Token** → Sent via HttpOnly cookie, expires in 6 months

### Default Test Users

```sql
-- Admin/Hotel Manager
Email: manager@test.com
Password: password123

-- Guest User
Email: guest@test.com
Password: password123
```

---

## 🏗️ Project Structure

```
src/main/java/niketeck/StayNest/
├── StayNestApplication.java          # Main Spring Boot app
├── config/
│   ├── CorsConfig.java               # CORS configuration
│   ├── OpenApiConfig.java            # Swagger/OpenAPI config
│   └── JacksonConfig.java            # JSON configuration
├── controller/
│   ├── AuthController.java           # Auth endpoints
│   ├── HotelController.java          # Hotel management (admin)
│   ├── HotelBrowseController.java    # Hotel search & browse
│   ├── RoomAdminController.java      # Room management (admin)
│   ├── BookingController.java        # Booking flow
│   ├── UserController.java           # User profile
│   ├── InventoryController.java      # Inventory management
│   ├── ChatController.java           # AI chatbot
│   └── WebhookController.java        # Stripe webhooks
├── service/
│   ├── AuthService.java              # Authentication logic
│   ├── HotelService.java             # Hotel operations
│   ├── RoomService.java              # Room operations
│   ├── BookingService.java           # Booking logic
│   ├── UserService.java              # User management
│   ├── GuestService.java             # Guest management
│   ├── InventoryService.java         # Inventory logic
│   ├── ChatService.java              # AI chat logic
│   └── PaymentService.java           # Payment processing
├── strategy/
│   └── PricingService.java           # Dynamic pricing strategies
├── entity/
│   ├── User.java                     # User entity
│   ├── Hotel.java                    # Hotel entity
│   ├── Room.java                     # Room entity
│   ├── Booking.java                  # Booking entity
│   ├── Guest.java                    # Guest entity
│   ├── Inventory.java                # Inventory entity
│   └── enums/                        # Enums (Role, BookingStatus, etc)
├── dto/
│   ├── AuthDto.java                  # Auth request/response
│   ├── HotelDto.java                 # Hotel DTO
│   ├── RoomDto.java                  # Room DTO
│   ├── BookingDto.java               # Booking DTO
│   ├── GuestDto.java                 # Guest DTO
│   ├── InventoryDto.java             # Inventory DTO
│   └── ...                           # Other DTOs
├── repository/
│   ├── UserRepository.java           # User JPA repository
│   ├── HotelRepository.java          # Hotel JPA repository
│   └── ...                           # Other repositories
├── security/
│   ├── WebSecurityConfig.java        # Spring Security config
│   ├── JWTAuthFilter.java            # JWT filter
│   └── JWTService.java               # JWT utilities
└── exception/
    ├── GlobalExceptionHandler.java   # Exception handling
    └── CustomExceptions.java         # Custom exceptions
```

---

## 🔌 Key API Endpoints

### Authentication
```
POST   /api/v1/auth/signup             Register new user
POST   /api/v1/auth/login              Login user
POST   /api/v1/auth/refresh            Refresh access token
```

### Hotels (Browse - Public)
```
GET    /api/v1/hotels                  Get all active hotels
POST   /api/v1/hotels/search           Search hotels (city, dates, rooms)
POST   /api/v1/hotels/semantic-search  AI semantic search
GET    /api/v1/hotels/{id}/info        Get hotel details
```

### Hotels (Admin Only)
```
POST   /api/v1/admin/hotels            Create hotel
GET    /api/v1/admin/hotels            Get my hotels
PUT    /api/v1/admin/hotels/{id}       Update hotel
DELETE /api/v1/admin/hotels/{id}       Delete hotel
PATCH  /api/v1/admin/hotels/{id}/activate  Activate hotel
GET    /api/v1/admin/hotels/{id}/bookings  Get hotel bookings
GET    /api/v1/admin/hotels/{id}/reports   Get booking reports
```

### Rooms (Admin Only)
```
POST   /api/v1/admin/hotels/{hotelId}/rooms            Create room
GET    /api/v1/admin/hotels/{hotelId}/rooms            Get rooms
PUT    /api/v1/admin/hotels/{hotelId}/rooms/{roomId}   Update room
DELETE /api/v1/admin/hotels/{hotelId}/rooms/{roomId}   Delete room
```

### Inventory (Admin Only)
```
GET    /api/v1/admin/inventory/rooms/{roomId}   Get inventory
PATCH  /api/v1/admin/inventory/rooms/{roomId}   Update inventory (bulk)
```

### Bookings
```
POST   /api/v1/bookings/init                          Initialize booking
POST   /api/v1/bookings/{id}/addGuests               Add guests
POST   /api/v1/bookings/{id}/payments                Create payment
GET    /api/v1/bookings/{id}/status                  Get booking status
POST   /api/v1/bookings/{id}/cancel                  Cancel booking
```

### User Profile
```
GET    /api/v1/users/profile                Get my profile
PATCH  /api/v1/users/profile               Update profile
GET    /api/v1/users/myBookings            Get my bookings
GET    /api/v1/users/guests                Get saved guests
POST   /api/v1/users/guests                Add guest
PUT    /api/v1/users/guests/{id}           Update guest
DELETE /api/v1/users/guests/{id}           Delete guest
```

### AI Features
```
POST   /api/v1/chat/message              Chat with AI assistant
POST   /api/v1/admin/hotels/{id}/generate-description     Generate hotel description
```

### Stripe Webhooks
```
POST   /api/v1/webhook/stripe            Stripe payment webhooks
```

---

## 💳 Payment Integration

### Stripe Setup

1. Get API keys from [Stripe Dashboard](https://dashboard.stripe.com)
2. Add to `application.properties`:
   ```properties
   stripe.secret.key=sk_test_YOUR_KEY
   stripe.webhook.secret=whsec_YOUR_WEBHOOK_SECRET
   ```
3. Set webhook endpoint in Stripe Dashboard:
   ```
   http://YOUR_DOMAIN/api/v1/webhook/stripe
   ```

### Booking Payment Flow

1. **Init Booking** → `POST /bookings/init` → Returns bookingId
2. **Add Guests** → `POST /bookings/{id}/addGuests`
3. **Create Payment** → `POST /bookings/{id}/payments` → Get Stripe checkout URL
4. **User Pays** → Opens Stripe checkout
5. **Webhook Confirmation** → Stripe confirms payment → Booking CONFIRMED
6. **Refunds** → `POST /bookings/{id}/cancel` → Refunds money to user

---

## 🤖 Spring AI Features

### 1. AI Chatbot (Google Gemini)

**Endpoint:**
```
POST /api/v1/chat/message
```

**Features:**
- Natural language hotel queries
- Database-powered answers
- Conversation history management
- Multi-turn dialogue

**Tools Available:**
- Search hotels by city
- Get hotel details
- Search by amenities
- Get available cities

### 2. Semantic Search (Vector Embeddings)

**Endpoint:**
```
POST /api/v1/hotels/semantic-search
```

**Features:**
- Search by meaning: "hill station with pool"
- "Near Taj Mahal" queries
- AI-powered relevance ranking
- pgvector similarity search

### 3. Auto Description Generator

**Endpoints:**
```
POST /api/v1/admin/hotels/{id}/generate-description
POST /api/v1/admin/hotels/{id}/rooms/{roomId}/generate-description
```

**Features:**
- AI-generated marketing descriptions
- Hotel manager can use for listings
- Uses Gemini 2.5 Flash model

---

## 💰 Dynamic Pricing Engine

The booking system uses a **Strategy Pattern** for flexible pricing:

1. **Base Price** - Set per room
2. **Surge Factor** - Peak season multiplier (set by manager)
3. **Occupancy Strategy** - Price increases as rooms fill
4. **Urgency Strategy** - Price increases as check-in date approaches
5. **Holiday Strategy** - Higher prices on holidays

**Final Price = Base × Surge × Occupancy × Urgency × Holiday**

---

## 🗄️ Database Schema

### Key Tables
- **users** - User accounts (guest, hotel_manager)
- **hotels** - Hotel listings
- **rooms** - Rooms per hotel
- **bookings** - Guest bookings
- **inventory** - Daily availability & pricing
- **guests** - Saved guest profiles
- **vector_store** - AI embeddings (pgvector)

---

## 🚀 Deployment

### Docker Build

```bash
docker build -t booknearme-backend:latest .
```

### Deploy to AWS ECS

1. Push Docker image to GHCR
2. Create ECS Task Definition with image
3. Create ECS Service in cluster
4. Attach Application Load Balancer

**See [AWS Deployment Guide](#aws-deployment-guide) below**

---

## 📊 AWS Deployment Guide

### Prerequisites
- AWS Account with RDS PostgreSQL 18
- AWS ECS Fargate cluster
- Application Load Balancer
- GitHub Actions enabled

### Step 1: Setup RDS Database

```bash
# RDS endpoint
booknearmedb.cjim0ucoi32v.ap-south-1.rds.amazonaws.com

# Enable pgvector
CREATE EXTENSION IF NOT EXISTS vector;
```

### Step 2: Update application.properties

```properties
spring.datasource.url=jdbc:postgresql://booknearme_db.amazonaws.com:5432/booknearme_db
spring.datasource.username=booknearme_db_username
spring.datasource.password=${DB_PASSWORD}
frontend.url=https://.vercel.app
```

### Step 3: Push to GitHub

```bash
git push origin main
```

GitHub Actions automatically:
- Builds Docker image
- Pushes to GHCR
- Triggers ECS deployment

### Step 4: Get Load Balancer URL

```bash
# From AWS Console
EC2 → Load Balancers → Get DNS Name
Example: alb-123456.ap-south-1.elb.amazonaws.com
```

---

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn test -Dtest=*IntegrationTest
```

### Load Testing (with Apache JMeter)
```bash
# Test concurrent hotel searches
jmeter -n -t tests/search-load-test.jmx
```

---

## 📝 Environment Variables

| Variable | Required | Description |
|---|---|---|
| `DB_PASSWORD` | ✅ | PostgreSQL password |
| `JWT_SECRET_KEY` | ✅ | JWT signing secret |
| `STRIPE_SECRET_KEY` | ✅ | Stripe API key |
| `STRIPE_WEBHOOK_SECRET` | ✅ | Stripe webhook secret |
| `GEMINI_API_KEY` | ✅ | Google AI Studio API key |
| `FRONTEND_URL` | ✅ | Frontend domain (CORS) |

---

## 🐛 Troubleshooting

### Database Connection Error
```
Check if PostgreSQL is running
Check connection string in application.properties
Verify pgvector extension: SELECT * FROM pg_extension WHERE extname = 'vector';
```

### JWT Token Invalid
```
Check jwt.secretKey matches in all environments
Verify token not expired (10 min access token, 6 months refresh)
Clear localStorage and login again
```

### CORS Error in Frontend
```
Update CorsConfig.java with frontend URL
Rebuild and redeploy
Clear browser cache
```

### Docker Build Fails
```
Check Java version: java -version
Check Maven cache: mvn clean
Build locally first: mvn clean package
```

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring AI Documentation](https://spring.io/projects/spring-ai)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [pgvector Documentation](https://github.com/pgvector/pgvector)
- [Stripe API Reference](https://stripe.com/docs/api)
- [JWT.io](https://jwt.io/)

---

## 🤝 Contributing

1. Fork repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

---

## 📄 License

This project is licensed under the MIT License - see LICENSE file for details.

---

## 👨‍💻 Author

**BookNearMe Team**

- GitHub: [@booknearme](https://github.com/booknearme)
- Email: support@booknearme.com
- Website: [booknearme.com](https://booknearme.com)

---

## 🎯 Roadmap

- [ ] Advanced analytics dashboard
- [ ] Email notifications
- [ ] SMS notifications
- [ ] Review & rating system
- [ ] Wishlist feature
- [ ] Multi-currency support
- [ ] Admin dashboard UI
- [ ] Mobile app

---

**Last Updated:** June 2026
