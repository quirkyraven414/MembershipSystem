# Membership Management System

A comprehensive Spring Boot application for managing tiered membership subscriptions with automatic tier progression, flexible pricing, and dynamic additional charges.

## Features

### 🎯 Core Functionality
- **Multi-tier Membership System** (Silver, Gold, Platinum)
- **Flexible Subscription Durations** (Monthly, Quarterly, Annual)
- **Membership Transitions** (Upgrade, Downgrade, Cancel)
- **Prorated Pricing** for upgrades with credit calculation
- **Transaction History** and audit logging

### 📊 Tier Progression System
- **Automatic Tier Evaluation** after each order
- **SQL-based Rules Engine** with configurable criteria:
  - Order count (e.g., 10+ orders → Gold)
  - Order value (e.g., ₹50,000/month → Platinum)
  - User cohorts (e.g., VIP customers → Gold)
- **Time Windows**: All-time, current month, last 30 days
- **Priority-based** rule evaluation

### 💰 Additional Charges System
- **MongoDB-based** flexible JSON rules
- **Multiple Calculation Types**:
  - Percentage (e.g., 5% convenience charge)
  - Fixed amount (e.g., ₹50 delivery fee)
  - Tiered (e.g., ₹50 for orders <₹500, ₹30 for ₹500-₹1000)
- **Transaction-specific** charges (upgrade, downgrade, new subscription)
- **Tier-specific** and order value-based filtering

## Tech Stack

- **Backend**: Spring Boot 3.2.0
- **Language**: Java 17
- **Databases**:
  - H2 (in-memory SQL) - Main data storage
  - MongoDB - Additional charges rules
- **Build Tool**: Maven
- **API Documentation**: Postman Collection

## Project Structure

```
src/main/java/com/membership/
├── controller/          # REST API endpoints
│   ├── MembershipController.java
│   ├── OrderController.java
│   ├── TierRuleController.java
│   └── AdditionalChargeController.java
├── service/            # Business logic
│   ├── UserMembershipService.java
│   ├── TierProgressionService.java
│   ├── PricingCalculationService.java
│   └── AdditionalChargeService.java
├── model/              # JPA entities
│   ├── User.java
│   ├── Membership.java
│   ├── Order.java
│   └── TierProgressionRule.java
├── document/           # MongoDB documents
│   └── AdditionalCharge.java
├── repository/         # Data access layer
├── transition/         # Transition rules engine
└── plans/             # Membership tier definitions
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MongoDB (Docker recommended)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/quirkyraven414/MembershipSystem.git
cd MembershipSystem
```

2. **Start MongoDB**
```bash
docker run -d -p 27017:27017 --name mongodb-membership mongo:latest --noauth
```

3. **Build the project**
```bash
mvn clean install
```

4. **Run the application**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Database Access

**H2 Console**: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/membershipdb`
- Username: `sa`
- Password: (empty)

## API Endpoints

### User Management
- `POST /api/users` - Create user
- `GET /api/users` - Get all users
- `POST /api/users/search` - Search user by ID

### Membership Management
- `POST /api/memberships` - Create membership
- `GET /api/memberships/user/{userId}` - Get current membership
- `POST /api/memberships/upgrade` - Upgrade membership
- `POST /api/memberships/downgrade` - Downgrade membership
- `POST /api/memberships/cancel` - Cancel membership
- `GET /api/memberships/history/{userId}` - Get membership history
- `GET /api/memberships/transactions/{userId}` - Get transaction history

### Order Management
- `POST /api/orders` - Create order (auto tier evaluation)
- `GET /api/orders/user/{userId}` - Get user orders
- `POST /api/orders/user/{userId}/evaluate-tier` - Evaluate tier eligibility

### Tier Progression Rules
- `GET /api/tier-rules` - List all rules
- `POST /api/tier-rules` - Create rule
- `PUT /api/tier-rules/{id}` - Update rule
- `DELETE /api/tier-rules/{id}` - Delete rule

### Additional Charges
- `GET /api/charges` - List all charges
- `POST /api/charges` - Create charge
- `PUT /api/charges/{id}` - Update charge
- `DELETE /api/charges/{id}` - Delete charge
- `POST /api/charges/evaluate` - Test charge evaluation

### Pricing Estimates
- `POST /api/pricing/estimate` - Get pricing estimate for transition

## Usage Examples

### Create a User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'
```

### Create Membership
```bash
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"tier":"SILVER","duration":"MONTHLY"}'
```

### Place Order (Auto Tier Evaluation)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"price":5000}'
```

### Create Tier Progression Rule
```bash
curl -X POST http://localhost:8080/api/tier-rules \
  -H "Content-Type: application/json" \
  -d '{
    "ruleName":"GOLD_ORDER_COUNT",
    "targetTier":"GOLD",
    "criteriaType":"ORDER_COUNT",
    "operator":">=",
    "thresholdValue":10,
    "timeWindow":"ALL_TIME",
    "active":true,
    "priority":1
  }'
```

### Create Additional Charge
```bash
curl -X POST http://localhost:8080/api/charges \
  -H "Content-Type: application/json" \
  -d '{
    "chargeName":"DOWNGRADE_CONVENIENCE_CHARGE",
    "chargeType":"CONVENIENCE",
    "active":true,
    "rules":{
      "applicableFor":["DOWNGRADE"],
      "calculationType":"PERCENTAGE",
      "value":5,
      "description":"5% convenience charge for downgrades"
    }
  }'
```

## Configuration

### Application Properties
```properties
# Server
server.port=8080

# H2 Database
spring.datasource.url=jdbc:h2:file:./data/membershipdb
spring.jpa.hibernate.ddl-auto=update

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/membership_db
spring.data.mongodb.database=membership_db
```

## Business Rules

### Membership Transitions
- **Upgrades**: Allowed anytime with prorated pricing
- **Downgrades**: Only at subscription end (no proration)
- **Cancellations**: 
  - Silver: Anytime
  - Gold/Platinum: Only at subscription end

### Tier Progression
- Evaluated automatically after each order
- Multiple rules can qualify a user for different tiers
- Highest qualifying tier is selected
- Only upgrades are automatic (no downgrades)

### Pricing
- Base price determined by tier and duration
- Upgrades: Credit applied for unused days
- Downgrades: No credit, effective at subscription end
- Additional charges applied based on transaction type

## Testing

Import the Postman collection (`Membership_System_API.postman_collection.json`) for complete API testing.

## Default Tier Rules

The system initializes with 9 default tier progression rules:

**Gold Tier**:
- 10+ orders (all time)
- ₹10,000+ monthly order value
- 5+ orders in last 30 days
- VIP_CUSTOMERS cohort
- EARLY_ADOPTERS cohort

**Platinum Tier**:
- 50+ orders (all time)
- ₹50,000+ monthly order value
- ₹100,000+ total order value
- PREMIUM_CUSTOMERS cohort

## License

This project is licensed under the MIT License.

## Author

Krishna Kishore

## Repository

https://github.com/quirkyraven414/MembershipSystem
