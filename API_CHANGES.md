# API Changes - MembershipId Now in Request Body

## ✅ Updated Endpoints

All membership transition endpoints now accept `membershipId` in the **request body** instead of path parameters.

---

### **1. Upgrade Membership**

**OLD**:
```http
POST /api/memberships/{membershipId}/upgrade
Body: {"userId": 1, "newTier": "GOLD"}
```

**NEW**:
```http
POST /api/memberships/upgrade
Body: {
  "membershipId": 1,
  "userId": 1,
  "newTier": "GOLD"
}
```

---

### **2. Downgrade Membership**

**OLD**:
```http
POST /api/memberships/{membershipId}/downgrade
Body: {"userId": 1, "newTier": "SILVER", "isSubscriptionEnd": true}
```

**NEW**:
```http
POST /api/memberships/downgrade
Body: {
  "membershipId": 1,
  "userId": 1,
  "newTier": "SILVER",
  "isSubscriptionEnd": true
}
```

---

### **3. Cancel Membership**

**OLD**:
```http
POST /api/memberships/{membershipId}/cancel?userId=1&isSubscriptionEnd=false
```

**NEW**:
```http
POST /api/memberships/cancel
Body: {
  "membershipId": 1,
  "userId": 1,
  "isSubscriptionEnd": false
}
```

---

## Updated Request Body Schema

### **TransitionRequest**
```json
{
  "membershipId": 1,      // NEW - now in body
  "userId": 1,
  "newTier": "GOLD",
  "isSubscriptionEnd": false
}
```

---

## Example Requests

### Upgrade Example
```bash
curl -X POST http://localhost:8080/api/memberships/upgrade \
  -H "Content-Type: application/json" \
  -d '{
    "membershipId": 1,
    "userId": 1,
    "newTier": "GOLD"
  }'
```

### Downgrade Example
```bash
curl -X POST http://localhost:8080/api/memberships/downgrade \
  -H "Content-Type: application/json" \
  -d '{
    "membershipId": 1,
    "userId": 1,
    "newTier": "SILVER",
    "isSubscriptionEnd": true
  }'
```

### Cancel Example
```bash
curl -X POST http://localhost:8080/api/memberships/cancel \
  -H "Content-Type: application/json" \
  -d '{
    "membershipId": 1,
    "userId": 1,
    "isSubscriptionEnd": false
  }'
```

---

## Summary

✅ **Cleaner API**: All data in request body
✅ **Consistent**: All endpoints follow same pattern
✅ **Easier to use**: No need to construct URLs with IDs

**Note**: Update your Postman requests to use the new format!
