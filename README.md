# MegaCityCab - Ride Booking Management System

![MegaCityCab](https://img.shields.io/badge/MegaCityCab-v6.0-blue)
![Java](https://img.shields.io/badge/Java-Servlets-orange)
![Database](https://img.shields.io/badge/Database-MSSQL-brightgreen)
![Frontend](https://img.shields.io/badge/Frontend-AJAX-yellow)

## 📋 Overview

MegaCityCab is a comprehensive ride booking management system designed for Colombo City's popular cab service. The system automates and streamlines the process of booking rides, managing drivers, calculating fares, and handling payments. Built using Java Servlets with AJAX for the frontend and MSSQL for data storage, MegaCityCab replaces the previous manual system with an efficient, user-friendly digital solution.

## 🚀 Features

### 🔐 Authentication System
- Secure login for passengers, drivers, and administrators
- Role-based access control and permissions

### 👤 Passenger Features
- Book rides with customizable pickup and drop-off locations
- Select vehicle type (Three-wheeler, Car, Van)
- Choose payment methods (Cash, Card, Wallet)
- View booking history and active rides
- Cancel bookings before driver assignment
- Manage wallet balance and transactions
- Add/edit/delete payment methods

### 🚕 Driver Features
- View and accept assigned rides
- Access passenger details and ride information
- Complete or cancel rides
- Track earnings and ride history
- Pay admin commission (30% of fare)
- Manage earnings and withdrawal to bank accounts
- View transaction history

### 👑 Admin Features
- View all bookings in the system
- Set and manage ride fares based on distance and vehicle type
- Assign drivers to bookings
- Manage vehicle information (add, edit, delete)
- Manage user accounts (passengers and drivers)
- Monitor wallet transactions and commission payments
- Generate reports and statements

## 📊 Fare Structure
- Three-wheeler: LKR90/km
- Car: LKR120/km
- Van: LKR150/km
- Admin commission: 30% of total fare

## 💻 Technical Stack

- **Backend:** Java Servlets
- **Frontend:** AJAX with JSON responses
- **Database:** MSSQL
- **Architecture:** SOLID principles

## 🛠️ Installation and Setup

### Prerequisites
- JDK 8 or higher
- Apache Tomcat Server
- MSSQL Server
- Git

### Steps to Install

1. Clone the repository:
   ```
   git clone https://github.com/mohrashard/MegaCityCab.git
   ```

2. Navigate to the project directory:
   ```
   cd MegaCityCab
   ```

3. Configure the database connection in `src/main/resources/database.properties`:
   ```
   db.url=jdbc:sqlserver://localhost:1433;databaseName=MegaCityCab
   db.username=your_username
   db.password=your_password
   ```

4. Build the project:
   ```
   mvn clean install
   ```

5. Deploy the WAR file to your Tomcat server.

6. Access the application at `http://localhost:8080/MegaCityCab`

## 📱 User Interfaces

- **Login Page:** Authentication for all users
- **Passenger Dashboard:** Booking form, ride history, wallet management
- **Driver Dashboard:** Assigned rides, earnings, commission payment
- **Admin Panel:** Booking management, driver assignment, user management, vehicle management

## 🔄 Workflow

1. Passenger creates a booking by providing pickup and drop-off locations
2. Admin assigns a fare based on distance and vehicle type
3. Admin assigns an available driver to the booking
4. Driver accepts the ride and contacts the passenger
5. Passenger pays for the ride using the selected payment method
6. Driver completes the ride and pays admin commission
7. Transaction is recorded in the system

## 👥 System Roles

| Action | Passenger | Driver | Admin |
|--------|-----------|--------|-------|
| Book Ride | ✅ | ❌ | ❌ |
| View Bookings | ✅ | ✅ | ✅ |
| Cancel Ride | ✅ | ✅ | ❌ |
| Assign Driver | ❌ | ❌ | ✅ |
| Manage Wallet | ✅ | ✅ | ✅ |
| Withdraw Money | ❌ | ✅ | ✅ |
| Set Ride Fare | ❌ | ❌ | ✅ |
| Manage Vehicles | ❌ | ❌ | ✅ |
| Manage Users | ❌ | ❌ | ✅ |

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request



## 📞 Contact

For any inquiries or support, please contact:
- Email: mohrashard@gmail.com
- GitHub: https://github.com/mohrashard

---

© 2025 MegaCityCab. All Rights Reserved.
