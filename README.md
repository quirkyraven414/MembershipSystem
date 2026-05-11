# Membership Management System

A production-ready membership management system with pro-rated pricing, extensible rules engine, and thread-safe operations.

## Architecture Highlights

### Design Patterns
- **Strategy Pattern**: Membership tiers (Silver, Gold, Platinum) implement `MembershipTier` interface
- **Factory Pattern**: `MembershipFactory` creates tier instances
- **Repository Pattern**: Generic `Repository<T, ID>` interface for data access
- **Service Layer**: Business logic separated from controllers

### Thread Safety
- **ConcurrentHashMap**: All repositories use thread-safe collections
- **AtomicLong**: Thread-safe ID generation
- **Immutable DTOs**: Response objects are immutable

### Extensibility
- **Pricing Adjustments**: Pluggable pricing rules via `PricingAdjustmentRepository`
- **Transition Rules**: Configurable upgrade/downgrade rules
- **Duration Strategy**: Enum-based subscription periods

## Build & Run

```bash
mvn clean install
mvn spring-boot:run
```

Server starts on: `http://localhost:8080`

## API Endpoints

### Users
- `POST /api/users` - Create user
- `GET /api/users/{userId}` - Get user
- `GET /api/users` - List all users

### Memberships
- `GET /api/memberships/user/{userId}` - Get current membership
- `POST /api/memberships` - Create membership
- `POST /api/memberships/upgrade` - Upgrade tier
- `POST /api/memberships/downgrade` - Downgrade tier
- `POST /api/memberships/cancel/{userId}` - Cancel membership
- `GET /api/memberships/history/{userId}` - Get history
- `GET /api/memberships/transactions/{userId}` - Get transactions

### Rules
- `GET /api/rules` - Get all transition rules

## Example Usage

### 1. Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'
```

### 2. Create Membership
```bash
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"tier":"SILVER","duration":"ANNUAL"}'
```

### 3. Upgrade
```bash
curl -X POST http://localhost:8080/api/memberships/upgrade \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"newTier":"GOLD"}'
```

### 4. Get Current Membership
```bash
curl http://localhost:8080/api/memberships/user/1
```

### 5. View Rules
```bash
curl http://localhost:8080/api/rules
```

## Features

✅ Pro-rated pricing on upgrades (credit unused time)  
✅ Configurable pricing adjustments (discounts/penalties)  
✅ One downgrade per membership cycle  
✅ Complete audit trail (history + transactions)  
✅ Thread-safe concurrent operations  
✅ Extensible rules engine  
✅ RESTful API with proper error handling

## Demo

Run the standalone demo:
```bash
mvn exec:java -Dexec.mainClass="com.membership.demo.MembershipDemo"
```
