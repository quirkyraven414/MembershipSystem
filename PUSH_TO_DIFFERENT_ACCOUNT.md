# Push to Different Git Account

## Method 1: Using HTTPS with Token (Recommended)

### Step 1: Create Personal Access Token
1. Login to your **other GitHub account**
2. Go to: Settings → Developer settings → Personal access tokens → Tokens (classic)
3. Click "Generate new token (classic)"
4. Select scopes: `repo` (full control)
5. Generate and **copy the token**

### Step 2: Add Files and Commit
```bash
cd /Users/krishna.kishore/membership-program
git add .
git commit -m "Initial commit: Membership Management System"
```

### Step 3: Add Remote with Token
```bash
# Replace YOUR_TOKEN and YOUR_USERNAME
git remote add origin https://YOUR_TOKEN@github.com/YOUR_USERNAME/membership-management-system.git
git branch -M main
git push -u origin main
```

**Example**:
```bash
git remote add origin https://ghp_abc123xyz@github.com/johndoe/membership-management-system.git
```

---

## Method 2: Using SSH (More Secure)

### Step 1: Generate SSH Key for Different Account
```bash
ssh-keygen -t ed25519 -C "your-other-email@example.com" -f ~/.ssh/id_ed25519_other
```

### Step 2: Add SSH Key to GitHub
```bash
# Copy the public key
cat ~/.ssh/id_ed25519_other.pub
```

1. Login to your **other GitHub account**
2. Go to: Settings → SSH and GPG keys → New SSH key
3. Paste the public key
4. Save

### Step 3: Configure SSH for This Repo
```bash
# Create/edit SSH config
nano ~/.ssh/config
```

Add:
```
Host github-other
  HostName github.com
  User git
  IdentityFile ~/.ssh/id_ed25519_other
```

### Step 4: Add Remote with SSH
```bash
cd /Users/krishna.kishore/membership-program
git add .
git commit -m "Initial commit: Membership Management System"
git remote add origin git@github-other:YOUR_USERNAME/membership-management-system.git
git branch -M main
git push -u origin main
```

---

## Method 3: Configure Local Git User (Per Repository)

Set different user for this repository only:

```bash
cd /Users/krishna.kishore/membership-program

# Set user for this repo only
git config user.name "Your Other Name"
git config user.email "your-other-email@example.com"

# Add and commit
git add .
git commit -m "Initial commit: Membership Management System"

# Add remote (use HTTPS with token or SSH)
git remote add origin https://YOUR_TOKEN@github.com/YOUR_USERNAME/membership-management-system.git
git branch -M main
git push -u origin main
```

---

## Quick Start (Easiest)

1. **Create GitHub repo** in your other account
2. **Generate Personal Access Token** (Settings → Developer settings)
3. **Run these commands**:

```bash
cd /Users/krishna.kishore/membership-program

# Configure local user (optional)
git config user.name "Your Other Name"
git config user.email "your-other-email@example.com"

# Add all files
git add .

# Commit
git commit -m "Initial commit: Membership Management System with tier progression and additional charges"

# Add remote with token
git remote add origin https://YOUR_TOKEN@github.com/YOUR_USERNAME/membership-management-system.git

# Push
git branch -M main
git push -u origin main
```

---

## Verify

After pushing, visit:
```
https://github.com/YOUR_USERNAME/membership-management-system
```

You should see all your code! 🎉

---

## Troubleshooting

### Error: "Authentication failed"
- Check your token is correct
- Make sure token has `repo` scope
- Token might be expired - generate a new one

### Error: "Permission denied"
- Using SSH? Make sure SSH key is added to the correct GitHub account
- Check SSH config is correct

### Error: "Repository not found"
- Make sure repository exists in your other account
- Check the repository URL is correct
- Repository might be private - make sure you have access

---

## Remove Old Remote (if needed)

If you accidentally added wrong remote:
```bash
git remote remove origin
```

Then add the correct one.
