# Git Upload Guide

## Step 1: Add All Files

```bash
cd /Users/krishna.kishore/membership-program
git add .
```

## Step 2: Create Initial Commit

```bash
git commit -m "Initial commit: Membership Management System with tier progression and additional charges"
```

## Step 3: Create GitHub Repository

1. Go to https://github.com
2. Click "New repository"
3. Name it: `membership-management-system`
4. Don't initialize with README (we already have code)
5. Click "Create repository"

## Step 4: Link Local to Remote

Copy the commands from GitHub (replace with your actual repo URL):

```bash
git remote add origin https://github.com/YOUR_USERNAME/membership-management-system.git
git branch -M main
git push -u origin main
```

## Step 5: Push to GitHub

```bash
git push -u origin main
```

---

## What Will Be Uploaded

✅ **Source Code**
- Controllers (User, Membership, Order, Tier Rules, Additional Charges)
- Services (Business logic)
- Models & DTOs
- Repositories

✅ **Configuration**
- `pom.xml` (Maven dependencies)
- `application.properties` (H2 + MongoDB config)
- `.gitignore` (excludes target/, data/, logs)

✅ **Documentation**
- `README.md`
- `API_CHANGES.md`
- `TIER_PROGRESSION_GUIDE.md`
- `ADDITIONAL_CHARGES_EXAMPLES.md`

✅ **Postman Collection**
- `Membership_System_API.postman_collection.json`

❌ **Excluded** (via .gitignore)
- `target/` (build files)
- `data/` (H2 database files)
- `*.log` (log files)
- `.DS_Store` (Mac files)

---

## Alternative: Using VS Code

1. Open Source Control panel (Ctrl+Shift+G)
2. Click "Initialize Repository"
3. Stage all changes (+ icon)
4. Enter commit message
5. Click "Publish to GitHub"
6. Select public/private
7. Done!

---

## Verify Upload

After pushing, check:
- https://github.com/YOUR_USERNAME/membership-management-system

You should see all your files! 🎉
