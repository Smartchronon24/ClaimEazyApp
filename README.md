# 🚀 ClaimEazy – Insurance Management App

ClaimEazy is a full-stack insurance management application designed to streamline customer data, claims processing, policy management, and payments. It supports role-based access for different types of users such as clients, approvers, ETL users, and admins.

---

## 📱 Features

### 👤 Authentication & User Roles

* Secure login system
* Role-based access control:

  * **Client** → Create and track claims
  * **Approver** → Approve/reject claims
  * **ETL** → View analytics and data
  * **Admin** → Full control over system

---

### 📊 Core Functionalities

#### 🧾 Customer Management

* Create and manage customer profiles
* Link users with customer IDs

#### 📄 Claims Processing

* Create new insurance claims
* Track claim status (Pending / Approved / Rejected)
* Automatic customer linking for claims

#### 📑 Policy Management

* Store and manage insurance policies
* Track coverage and premium details

#### 💳 Payment Tracking

* Record payments linked to policies
* Monitor payment status

---

## 🏗️ Tech Stack

### 📱 Frontend (Android)

* Kotlin
* Jetpack Compose
* MVVM Architecture

### ⚙️ Backend

* Python (Flask)
* REST APIs

### 🗄️ Database

* MySQL (Azure Database for MySQL)

### ☁️ Deployment

* Azure App Service
* GitHub Actions (CI/CD)

---

## 🔐 Architecture Overview

```
Android App → Flask API → MySQL Database
```

* Android app communicates via REST APIs
* Flask handles business logic and validation
* MySQL stores all persistent data

---

## 🚀 Getting Started

### 🔧 Backend Setup

1. Clone the repository:

```bash
git clone https://github.com/Smartchronon24/ClaimEazy-backend.git
cd ClaimEazy-backend
```

2. Install dependencies:

```bash
pip install -r requirements.txt
```

3. Run the Flask app:

```bash
python app.py
```

---

### 📱 Android Setup

1. Open project in Android Studio
2. Build APK:

```text
Build → Build APK(s)
```

3. Install APK on your device

---

## 📦 APK Download

👉 Download latest version from:
**GitHub Releases section**

---

## 🧪 API Example

### Login

```
POST /login
```

**Request:**

```json
{
  "cust_id": "CUST001",
  "password": "1234"
}
```

**Response:**

```json
{
  "message": "Login successful",
  "user_id": "USR001",
  "role": "CLIENT",
  "cust_id": "CUST001"
}
```

---

## 🔒 Security Notes

* Sensitive files excluded using `.gitignore`
* No Firebase / tracking (F-Droid compatible branch)
* Role-based authorization enforced in backend

---

## 📌 Future Improvements

* SMS-based authentication
* Push notifications
* Advanced analytics dashboard
* Play Store deployment
* F-Droid release support

---

## 👨‍💻 Author

**Navaneth Anand**

---

## ⭐ Contributing

Feel free to fork the repo and submit pull requests!

---

## 📜 License

This project is open-source and available under the MIT License.
