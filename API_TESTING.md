# API Testing Guide

Complete curl commands to test all membership system APIs.

## Prerequisites

Start the server:
```bash
mvn spring-boot:run
```

Server runs on: `http://localhost:8080`

---

## 1. User Management APIs

### Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com"
  }'
```

**Expected Response:**
```json
{
  "userId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "createdAt": "2026-05-11T07:00:00"
}
```

### Get All Users
```bash
curl http://localhost:8080/api/users
```

### Get User by ID
```bash
curl http://localhost:8080/api/users/1
```

---

## 2. Membership Management APIs

### Create Membership
```bash
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "tier": "SILVER",
    "duration": "ANNUAL"
  }'
```

**Valid Tiers:** `SILVER`, `GOLD`, `PLATINUM`  
**Valid Durations:** `MONTHLY`, `QUARTERLY`, `ANNUAL`

**Expected Response:**
```json
{
  "membershipId": 1,
  "userId": 1,
  "tier": "SILVER",
  "duration": "ANNUAL",
  "startDate": "2026-05-11",
  "expiryDate": "2027-05-11",
  "basePrice": 1080.0,
  "paidPrice": 1080.0,
  "status": "ACTIVE",
  "hasDowngraded": false,
  "downgradeDate": null
}
```

### Get User's Current Membership
```bash
curl http://localhost:8080/api/memberships/user/1
```

---

## 3. Membership Transition APIs

### Upgrade Membership
```bash
curl -X POST http://localhost:8080/api/memberships/1/upgrade \
  -H "Content-Type: application/json" \
  -d '{
    "newTier": "GOLD",
    "newDuration": "ANNUAL"
  }'
```

**Response includes:**
- Pro-rated pricing
- Credit from unused time
- 10% upgrade discount applied
- New membership details

### Downgrade Membership
```bash
curl -X POST http://localhost:8080/api/memberships/1/downgrade \
  -H "Content-Type: application/json" \
  -d '{
    "newTier": "SILVER",
    "newDuration": "ANNUAL"
  }'
```

**Note:** Only one downgrade allowed per membership cycle.

### Cancel Membership
```bash
curl -X POST http://localhost:8080/api/memberships/1/cancel
```

---

## 4. History & Transaction APIs

### Get Membership History
```bash
curl http://localhost:8080/api/memberships/history/1
```

**Response:** All membership changes for user (upgrades, downgrades, cancellations)

### Get Transaction Logs
```bash
curl http://localhost:8080/api/memberships/transactions/1
```

**Response:** All payment transactions with pricing details

---

## 5. Rules API

### Get All Transition Rules
```bash
curl http://localhost:8080/api/rules
```

**Response:** All upgrade/downgrade/cancel rules with conditions

---

## Complete Test Workflow

### Step 1: Create Users
```bash
# User 1
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice Smith","email":"alice@example.com"}'

# User 2
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Bob Johnson","email":"bob@example.com"}'
```

### Step 2: Create Memberships
```bash
# Alice - Silver Annual
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"tier":"SILVER","duration":"ANNUAL"}'

# Bob - Gold Monthly
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -d '{"userId":2,"tier":"GOLD","duration":"MONTHLY"}'
```

### Step 3: Upgrade Alice to Gold
```bash
curl -X POST http://localhost:8080/api/memberships/1/upgrade \
  -H "Content-Type: application/json" \
  -d '{"newTier":"GOLD","newDuration":"ANNUAL"}'
```

### Step 4: Upgrade Alice to Platinum
```bash
curl -X POST http://localhost:8080/api/memberships/1/upgrade \
  -H "Content-Type: application/json" \
  -d '{"newTier":"PLATINUM","newDuration":"ANNUAL"}'
```

### Step 5: Downgrade Alice to Gold (First Downgrade - Allowed)
```bash
curl -X POST http://localhost:8080/api/memberships/1/downgrade \
  -H "Content-Type: application/json" \
  -d '{"newTier":"GOLD","newDuration":"ANNUAL"}'
```

### Step 6: Try Second Downgrade (Should Fail)
```bash
curl -X POST http://localhost:8080/api/memberships/1/downgrade \
  -H "Content-Type: application/json" \
  -d '{"newTier":"SILVER","newDuration":"ANNUAL"}'
```

**Expected:** Error message about downgrade limit

### Step 7: View Alice's History
```bash
curl http://localhost:8080/api/memberships/history/1
```

### Step 8: View Alice's Transactions
```bash
curl http://localhost:8080/api/memberships/transactions/1
```

### Step 9: Cancel Bob's Membership
```bash
curl -X POST http://localhost:8080/api/memberships/2/cancel
```

### Step 10: View All Rules
```bash
curl http://localhost:8080/api/rules
```

---

## Pricing Examples

### Silver Pricing
- Monthly: $100 × 1 = $100
- Quarterly: $100 × 3 × 0.95 = $285
- Annual: $100 × 12 × 0.90 = $1,080

### Gold Pricing
- Monthly: $200 × 1 = $200
- Quarterly: $200 × 3 × 0.95 = $570
- Annual: $200 × 12 × 0.90 = $2,160

### Platinum Pricing
- Monthly: $300 × 1 = $300
- Quarterly: $300 × 3 × 0.95 = $855
- Annual: $300 × 12 × 0.90 = $3,240

---

## Pro-rated Pricing Example

**Scenario:** User has Silver Annual ($1,080) with 6 months remaining, upgrades to Gold Annual ($2,160)

1. **Unused Credit:** $1,080 × (6/12) = $540
2. **New Price:** $2,160
3. **Discount (10%):** $2,160 × 0.10 = $216
4. **Final Price:** $2,160 - $540 - $216 = $1,404

---

## Error Scenarios to Test

### Invalid Tier
```bash
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"tier":"INVALID","duration":"ANNUAL"}'
```

### Duplicate Email
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Duplicate","email":"alice@example.com"}'
```

### Non-existent User
```bash
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -d '{"userId":999,"tier":"SILVER","duration":"ANNUAL"}'
```

### Invalid Transition
```bash
# Try to downgrade from Silver (should fail - no lower tier)
curl -X POST http://localhost:8080/api/memberships/1/downgrade \
  -H "Content-Type: application/json" \
  -d '{"newTier":"BRONZE","newDuration":"ANNUAL"}'
```

---

## H2 Database Console

Access the database:
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/membershipdb
Username: sa
Password: (leave empty)
```

**Tables to explore:**
- `USERS`
- `MEMBERSHIPS`
- `MEMBERSHIP_HISTORY`
- `TRANSACTION_LOGS`
- `PRICING_ADJUSTMENTS`

---

## Tips

1. **Pretty Print JSON:** Add `| jq` to curl commands (requires jq installation)
   ```bash
   curl http://localhost:8080/api/users | jq
   ```

2. **Save Response:** Use `-o` flag
   ```bash
   curl http://localhost:8080/api/users -o users.json
   ```

3. **Verbose Mode:** Add `-v` for debugging
   ```bash
   curl -v http://localhost:8080/api/users
   ```

4. **Check Server Status:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```
   (If actuator is enabled)
