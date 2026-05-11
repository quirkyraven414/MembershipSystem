# API Error Handling Documentation

## Error Response Format

All API errors now return a consistent JSON structure:

```json
{
  "error": "Error Type",
  "message": "Detailed error message",
  "timestamp": "2026-05-11T01:46:40Z"
}
```

## HTTP Status Codes

- **200 OK** - Successful operation
- **400 Bad Request** - Validation error, business rule violation, or user not found
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Unexpected server error

---

## User Management Errors

### Search Users - No Users Found (404)
**Request:**
```bash
curl -X POST http://localhost:8080/api/users/search \
  -H "Content-Type: application/json" \
  -d '{"email":"notfound@example.com"}'
```

**Response:**
```json
{
  "error": "User Not Found",
  "message": "No users found matching the provided criteria",
  "timestamp": "2026-05-11T01:46:40Z"
}
```

**Search Criteria:**
You can search by one or multiple criteria:
- `userId` - Search by user ID
- `email` - Search by email address
- `membershipId` - Find user by their membership ID

**Examples:**
```bash
# Search by email
curl -X POST http://localhost:8080/api/users/search \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com"}'

# Search by userId
curl -X POST http://localhost:8080/api/users/search \
  -H "Content-Type: application/json" \
  -d '{"userId":1}'

# Search by membershipId
curl -X POST http://localhost:8080/api/users/search \
  -H "Content-Type: application/json" \
  -d '{"membershipId":1}'

# Search by multiple criteria
curl -X POST http://localhost:8080/api/users/search \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","userId":1}'
```

### Create User - Email Already Exists (400)
**Request:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","email":"john@example.com"}'
```

**Response:**
```json
{
  "error": "User Already Exists",
  "message": "User with email john@example.com already exists",
  "timestamp": "2026-05-11T01:46:40Z"
}
```

---

## Create Membership Errors

### User Not Found (400)
**Request:**
```bash
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -d '{"userId":999,"tier":"SILVER","duration":"ANNUAL"}'
```

**Response:**
```json
{
  "error": "User Not Found",
  "message": "User not found with ID: 999",
  "timestamp": "2026-05-11T01:46:40Z"
}
```

### User Already Has Active Membership (400)
**Request:**
```bash
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"tier":"SILVER","duration":"ANNUAL"}'
```

**Response:**
```json
{
  "error": "Invalid State",
  "message": "User already has an active membership",
  "timestamp": "2026-05-11T01:46:40Z"
}
```

---

## Upgrade Membership Errors

### No Active Membership (400)
**Response:**
```json
{
  "error": "Upgrade Failed",
  "message": "No active membership found",
  "timestamp": "2026-05-11T01:46:40Z"
}
```

### Upgrade Not Allowed (400)
**Response:**
```json
{
  "error": "Upgrade Failed",
  "message": "Upgrade not allowed: Cannot upgrade from PLATINUM",
  "timestamp": "2026-05-11T01:46:40Z"
}
```

---

## Downgrade Membership Errors

### Downgrade Limit Exceeded (400)
**Request:**
```bash
curl -X POST http://localhost:8080/api/memberships/1/downgrade \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"newTier":"SILVER","newDuration":"ANNUAL"}'
```

**Response:**
```json
{
  "error": "Downgrade Failed",
  "message": "Downgrade denied: Already downgraded once in this cycle",
  "timestamp": "2026-05-11T01:46:40Z"
}
```

### No Active Membership (400)
**Response:**
```json
{
  "error": "Downgrade Failed",
  "message": "No active membership found",
  "timestamp": "2026-05-11T01:46:40Z"
}
```

### Downgrade Not Allowed (400)
**Response:**
```json
{
  "error": "Downgrade Failed",
  "message": "Downgrade not allowed: Cannot downgrade from SILVER",
  "timestamp": "2026-05-11T01:46:40Z"
}
```

---

## Cancel Membership Errors

### No Active Membership (400)
**Request:**
```bash
curl -X POST "http://localhost:8080/api/memberships/1/cancel?userId=1"
```

**Response:**
```json
{
  "error": "Cancellation Failed",
  "message": "No active membership found",
  "timestamp": "2026-05-11T01:46:40Z"
}
```

### Success Response (200)
**Response:**
```json
{
  "error": "Success",
  "message": "Membership cancelled successfully",
  "timestamp": "2026-05-11T01:46:40Z"
}
```

---

## Validation Rules

### User Validation
- ✅ Email must be unique when creating a user
- ✅ User must exist before creating membership
- ✅ User can only have one active membership at a time

### Upgrade Rules
- ✅ Can upgrade from SILVER → GOLD → PLATINUM
- ❌ Cannot upgrade from PLATINUM (highest tier)
- ✅ Unlimited upgrades allowed

### Downgrade Rules
- ✅ Can downgrade from PLATINUM → GOLD → SILVER
- ❌ Cannot downgrade from SILVER (lowest tier)
- ❌ Only ONE downgrade allowed per membership cycle
- ✅ Downgrade counter resets when membership expires or is upgraded

### Cancellation Rules
- ✅ Can cancel any active membership
- ✅ Can choose immediate or end-of-subscription cancellation

---

## Testing Error Scenarios

### 1. Test Duplicate Email
```bash
# Create first user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'

# Try to create second user with same email (should fail)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","email":"john@example.com"}'
```

### 2. Test User Not Found
```bash
# Create membership for non-existent user
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -d '{"userId":999,"tier":"SILVER","duration":"ANNUAL"}'
```

### 3. Test Duplicate Membership
```bash
# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com"}'

# Create first membership (should succeed)
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"tier":"SILVER","duration":"ANNUAL"}'

# Try to create second membership (should fail)
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"tier":"GOLD","duration":"ANNUAL"}'
```

### 4. Test Downgrade Limit
```bash
# Create Platinum membership
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"tier":"PLATINUM","duration":"ANNUAL"}'

# First downgrade (should succeed)
curl -X POST http://localhost:8080/api/memberships/1/downgrade \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"newTier":"GOLD","newDuration":"ANNUAL"}'

# Second downgrade (should fail)
curl -X POST http://localhost:8080/api/memberships/1/downgrade \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"newTier":"SILVER","newDuration":"ANNUAL"}'
```

---

## Error Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `error` | string | Error category/type |
| `message` | string | Human-readable error description |
| `timestamp` | string | ISO 8601 UTC timestamp (e.g., "2026-05-11T01:46:40Z") |

---

## Common Error Types

| Error Type | Meaning |
|------------|---------|
| `User Already Exists` | Email already registered in system |
| `User Not Found` | Requested user doesn't exist in system |
| `Invalid State` | Operation violates business rules |
| `Upgrade Failed` | Upgrade operation not allowed |
| `Downgrade Failed` | Downgrade operation not allowed |
| `Cancellation Failed` | Cancellation operation not allowed |
| `Internal Error` | Unexpected server error |

---

## Best Practices

1. **Always check HTTP status code** before parsing response
2. **Display `message` field** to end users for clarity
3. **Log `error` and `timestamp`** for debugging
4. **Handle 400 errors** gracefully with user-friendly messages
5. **Retry 500 errors** with exponential backoff
