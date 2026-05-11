# Additional Charges Configuration Examples

This document shows how to configure additional charges using the MongoDB-based JSON rules system.

## Example 1: 5% Convenience Charge on Downgrades

```json
{
  "chargeName": "DOWNGRADE_CONVENIENCE_CHARGE",
  "chargeType": "CONVENIENCE",
  "active": true,
  "rules": {
    "applicableFor": ["DOWNGRADE"],
    "calculationType": "PERCENTAGE",
    "value": 5,
    "tiers": [],
    "description": "5% convenience charge for downgrades"
  }
}
```

## Example 2: Delivery Charge Based on Order Value

```json
{
  "chargeName": "DELIVERY_CHARGE",
  "chargeType": "DELIVERY",
  "active": true,
  "rules": {
    "applicableFor": ["NEW_SUBSCRIPTION", "UPGRADE", "DOWNGRADE"],
    "calculationType": "TIERED",
    "tiers": [
      {
        "min": 0,
        "max": 500,
        "type": "FIXED",
        "value": 50
      },
      {
        "min": 501,
        "max": 1000,
        "type": "FIXED",
        "value": 30
      },
      {
        "min": 1001,
        "max": 999999,
        "type": "FIXED",
        "value": 0
      }
    ],
    "description": "Tiered delivery charge"
  }
}
```

## Example 3: Processing Fee for Specific Tiers

```json
{
  "chargeName": "PROCESSING_FEE",
  "chargeType": "PROCESSING",
  "active": true,
  "rules": {
    "applicableFor": ["NEW_SUBSCRIPTION", "UPGRADE"],
    "calculationType": "PERCENTAGE",
    "value": 2,
    "tiers": ["PLATINUM"],
    "minOrderValue": 1000,
    "description": "2% processing fee for Platinum tier orders above ₹1000"
  }
}
```

## Example 4: Fixed Service Charge

```json
{
  "chargeName": "SERVICE_CHARGE",
  "chargeType": "SERVICE",
  "active": true,
  "rules": {
    "applicableFor": ["NEW_SUBSCRIPTION", "UPGRADE", "DOWNGRADE"],
    "calculationType": "FIXED",
    "value": 25,
    "description": "Fixed service charge of ₹25"
  }
}
```

## Example 5: Premium Support Charge (Tier-Specific)

```json
{
  "chargeName": "PREMIUM_SUPPORT",
  "chargeType": "SUPPORT",
  "active": true,
  "rules": {
    "applicableFor": ["UPGRADE"],
    "calculationType": "FIXED",
    "value": 100,
    "tiers": ["GOLD", "PLATINUM"],
    "description": "Premium support setup fee"
  }
}
```

## Rules Schema Explanation

### Required Fields
- `chargeName`: Unique identifier for the charge
- `chargeType`: Category (CONVENIENCE, DELIVERY, PROCESSING, SERVICE, etc.)
- `active`: Boolean to enable/disable the charge
- `rules`: JSON object containing the charge logic

### Rules Object Fields

#### applicableFor (Array)
Transaction types where this charge applies:
- `NEW_SUBSCRIPTION`
- `UPGRADE`
- `DOWNGRADE`

#### calculationType (String)
How to calculate the charge:
- `PERCENTAGE`: Percentage of order value
- `FIXED`: Fixed amount
- `TIERED`: Different amounts based on order value ranges

#### value (Number)
- For `PERCENTAGE`: The percentage value (e.g., 5 for 5%)
- For `FIXED`: The fixed amount (e.g., 50 for ₹50)
- Not used for `TIERED` (uses tiers array instead)

#### tiers (Array) - Optional
Filter by membership tiers:
- Empty array `[]` = applies to all tiers
- `["SILVER", "GOLD"]` = applies only to these tiers

For `TIERED` calculation type, this contains tier ranges:
```json
{
  "min": 0,
  "max": 500,
  "type": "PERCENTAGE" or "FIXED",
  "value": 5
}
```

#### minOrderValue (Number) - Optional
Minimum order value for charge to apply

#### maxOrderValue (Number) - Optional
Maximum order value for charge to apply

#### description (String) - Optional
Human-readable description

## API Endpoints

### Create Charge
```
POST /api/charges
Content-Type: application/json

{
  "chargeName": "DOWNGRADE_CONVENIENCE_CHARGE",
  "chargeType": "CONVENIENCE",
  "active": true,
  "rules": { ... }
}
```

### Get All Charges
```
GET /api/charges
```

### Get Charge by ID
```
GET /api/charges/{id}
```

### Update Charge
```
PUT /api/charges/{id}
Content-Type: application/json

{
  "chargeName": "DOWNGRADE_CONVENIENCE_CHARGE",
  "chargeType": "CONVENIENCE",
  "active": false,
  "rules": { ... }
}
```

### Delete Charge
```
DELETE /api/charges/{id}
```

### Evaluate Charges (Test)
```
POST /api/charges/evaluate?transactionType=DOWNGRADE&tier=GOLD&orderValue=1000
```

## How Charges Are Applied

1. When a pricing calculation happens (upgrade, downgrade, new subscription)
2. The system queries MongoDB for all active charges
3. For each charge, it evaluates the rules:
   - Checks if `applicableFor` matches the transaction type
   - Checks if `tiers` filter matches (if specified)
   - Checks if order value is within `minOrderValue` and `maxOrderValue` (if specified)
4. If all conditions match, calculates the charge amount based on `calculationType`
5. Adds all applicable charges to the final price
6. Stores the charge details in the adjustment reason

## MongoDB Configuration

Add to `application.properties`:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/membership_db
spring.data.mongodb.database=membership_db
```

Or for MongoDB Atlas:
```properties
spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/membership_db
```
