# Tier Progression System Guide

## Overview

Automatic tier progression system that upgrades users based on configurable SQL rules. Rules are evaluated after every order placement.

---

## Database Tables

### **1. orders**
```sql
orderId    BIGINT PRIMARY KEY AUTO_INCREMENT
userId     BIGINT NOT NULL
price      DOUBLE NOT NULL
orderDate  TIMESTAMP NOT NULL
```

### **2. tier_progression_rules**
```sql
id              BIGINT PRIMARY KEY AUTO_INCREMENT
ruleName        VARCHAR UNIQUE NOT NULL
targetTier      VARCHAR NOT NULL          -- SILVER, GOLD, PLATINUM
criteriaType    VARCHAR NOT NULL          -- ORDER_COUNT, ORDER_VALUE, COHORT
operator        VARCHAR                   -- >=, >, =, <, <=
thresholdValue  DOUBLE                    -- Numeric threshold
timeWindow      VARCHAR                   -- ALL_TIME, CURRENT_MONTH, LAST_30_DAYS
cohortName      VARCHAR                   -- For COHORT type rules
active          BOOLEAN NOT NULL
priority        INTEGER                   -- Lower number = higher priority
createdAt       TIMESTAMP
updatedAt       TIMESTAMP
```

### **3. users** (updated)
Added field:
```sql
cohort  VARCHAR  -- User segment: VIP_CUSTOMERS, EARLY_ADOPTERS, etc.
```

---

## Rule Types

### **1. ORDER_COUNT**
Upgrade based on number of orders

**Example**: Upgrade to GOLD if user has 10+ orders
```json
{
  "ruleName": "GOLD_ORDER_COUNT",
  "targetTier": "GOLD",
  "criteriaType": "ORDER_COUNT",
  "operator": ">=",
  "thresholdValue": 10,
  "timeWindow": "ALL_TIME",
  "active": true,
  "priority": 1
}
```

**Time Windows**:
- `ALL_TIME`: Count all orders ever
- `CURRENT_MONTH`: Count orders in current calendar month
- `LAST_30_DAYS`: Count orders in last 30 days

---

### **2. ORDER_VALUE**
Upgrade based on total order value

**Example**: Upgrade to PLATINUM if monthly order value ≥ ₹50,000
```json
{
  "ruleName": "PLATINUM_MONTHLY_VALUE",
  "targetTier": "PLATINUM",
  "criteriaType": "ORDER_VALUE",
  "operator": ">=",
  "thresholdValue": 50000,
  "timeWindow": "CURRENT_MONTH",
  "active": true,
  "priority": 2
}
```

---

### **3. COHORT**
Upgrade based on user segment

**Example**: Upgrade to GOLD if user is in VIP_CUSTOMERS cohort
```json
{
  "ruleName": "GOLD_VIP_COHORT",
  "targetTier": "GOLD",
  "criteriaType": "COHORT",
  "operator": "=",
  "cohortName": "VIP_CUSTOMERS",
  "active": true,
  "priority": 3
}
```

---

## How It Works

### **Automatic Evaluation (After Order)**

1. User places order via `POST /api/orders`
2. Order is saved to database
3. System automatically evaluates all active tier rules
4. Returns order + tier evaluation result:

```json
{
  "order": {
    "orderId": 123,
    "userId": 1,
    "price": 5000,
    "orderDate": "2026-05-11T12:00:00"
  },
  "tierEvaluation": {
    "userId": 1,
    "currentTier": "SILVER",
    "qualifiedTier": "GOLD",
    "upgradeNeeded": true,
    "matchedRules": ["GOLD_ORDER_COUNT"]
  }
}
```

### **Manual Evaluation**

Trigger tier check without placing order:
```
POST /api/orders/user/{userId}/evaluate-tier
```

---

## Rule Evaluation Logic

1. **Fetch all active rules** ordered by priority
2. **Evaluate each rule** against user data:
   - ORDER_COUNT: Query order count from database
   - ORDER_VALUE: Sum order prices from database
   - COHORT: Check user's cohort field
3. **Apply operator** (>=, >, =, <, <=)
4. **Select highest qualifying tier**
5. **Check if upgrade needed** (qualified tier > current tier)

### **Tier Hierarchy**
```
PLATINUM (rank 3)
    ↑
GOLD (rank 2)
    ↑
SILVER (rank 1)
    ↑
NONE (rank 0)
```

Only **upgrades** are supported - no automatic downgrades.

---

## API Endpoints

### **Order Management**

#### Create Order (Auto Tier Evaluation)
```http
POST /api/orders
Content-Type: application/json

{
  "userId": 1,
  "price": 5000
}
```

#### Get User Orders
```http
GET /api/orders/user/{userId}
```

#### Evaluate User Tier
```http
POST /api/orders/user/{userId}/evaluate-tier
```

---

### **Tier Rule Management**

#### Get All Rules
```http
GET /api/tier-rules
```

#### Create Rule
```http
POST /api/tier-rules
Content-Type: application/json

{
  "ruleName": "GOLD_ORDER_COUNT",
  "targetTier": "GOLD",
  "criteriaType": "ORDER_COUNT",
  "operator": ">=",
  "thresholdValue": 10,
  "timeWindow": "ALL_TIME",
  "cohortName": null,
  "active": true,
  "priority": 1
}
```

#### Update Rule
```http
PUT /api/tier-rules/{id}
```

#### Delete Rule
```http
DELETE /api/tier-rules/{id}
```

---

## Example Scenarios

### **Scenario 1: Order Count Based**

**Setup**:
```sql
INSERT INTO tier_progression_rules VALUES
(1, 'GOLD_ORDER_COUNT', 'GOLD', 'ORDER_COUNT', '>=', 10, 'ALL_TIME', NULL, true, 1);
```

**Flow**:
1. User (currently SILVER) places 10th order
2. System evaluates: `countByUserId(1) = 10`
3. Rule matches: `10 >= 10` ✅
4. Response: `qualifiedTier = "GOLD"`, `upgradeNeeded = true`

---

### **Scenario 2: Monthly Value Based**

**Setup**:
```sql
INSERT INTO tier_progression_rules VALUES
(2, 'PLATINUM_MONTHLY_VALUE', 'PLATINUM', 'ORDER_VALUE', '>=', 50000, 'CURRENT_MONTH', NULL, true, 2);
```

**Flow**:
1. User (currently GOLD) places order worth ₹20,000
2. System calculates: `sumValueByUserIdAndDateAfter(1, startOfMonth) = ₹52,000`
3. Rule matches: `52000 >= 50000` ✅
4. Response: `qualifiedTier = "PLATINUM"`, `upgradeNeeded = true`

---

### **Scenario 3: Cohort Based**

**Setup**:
```sql
INSERT INTO tier_progression_rules VALUES
(3, 'GOLD_VIP_COHORT', 'GOLD', 'COHORT', '=', NULL, NULL, 'VIP_CUSTOMERS', true, 3);

UPDATE users SET cohort = 'VIP_CUSTOMERS' WHERE userId = 1;
```

**Flow**:
1. User places first order
2. System checks: `user.cohort = 'VIP_CUSTOMERS'`
3. Rule matches: cohort equals 'VIP_CUSTOMERS' ✅
4. Response: `qualifiedTier = "GOLD"`, `upgradeNeeded = true`

---

## Multiple Rules

When multiple rules match, the **highest tier** wins:

**Example**:
- Rule 1: ORDER_COUNT ≥ 10 → GOLD ✅
- Rule 2: ORDER_VALUE ≥ ₹50k → PLATINUM ✅

Result: User qualifies for **PLATINUM** (higher tier)

---

## Operations Team Guide

### **View All Rules**
```sql
SELECT * FROM tier_progression_rules WHERE active = true ORDER BY priority;
```

### **Add New Rule**
```sql
INSERT INTO tier_progression_rules 
(ruleName, targetTier, criteriaType, operator, thresholdValue, timeWindow, cohortName, active, priority)
VALUES 
('GOLD_5_ORDERS', 'GOLD', 'ORDER_COUNT', '>=', 5, 'ALL_TIME', NULL, true, 1);
```

### **Disable Rule**
```sql
UPDATE tier_progression_rules SET active = false WHERE ruleName = 'GOLD_ORDER_COUNT';
```

### **Change Threshold**
```sql
UPDATE tier_progression_rules 
SET thresholdValue = 15 
WHERE ruleName = 'GOLD_ORDER_COUNT';
```

### **Check User's Order Stats**
```sql
-- Total orders
SELECT COUNT(*) FROM orders WHERE userId = 1;

-- Total order value
SELECT SUM(price) FROM orders WHERE userId = 1;

-- Current month orders
SELECT COUNT(*) FROM orders 
WHERE userId = 1 
AND orderDate >= DATE_FORMAT(NOW(), '%Y-%m-01');
```

---

## Testing

### **1. Create Test User**
```http
POST /api/users
{
  "name": "Test User",
  "email": "test@example.com"
}
```

### **2. Create Tier Rules**
Use Postman collection: "Tier Progression Rules" folder

### **3. Place Orders**
```http
POST /api/orders
{
  "userId": 1,
  "price": 1000
}
```

### **4. Check Tier Evaluation**
```http
POST /api/orders/user/1/evaluate-tier
```

---

## Key Features

✅ **Automatic evaluation** after every order
✅ **SQL-based rules** - easy for ops team to manage
✅ **Multiple criteria types** - count, value, cohort
✅ **Flexible time windows** - all-time, monthly, 30-day
✅ **Priority-based** - control rule evaluation order
✅ **Upgrade only** - no automatic downgrades
✅ **H2 console access** - view/edit rules directly in database

---

## Future Enhancements

- Add tier downgrade support
- Add AND/OR rule combinations
- Add scheduled batch evaluation
- Add tier change notifications
- Add tier history tracking
