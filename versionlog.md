# MegaCityCab Web Application - Version History & Changelog

## Introduction

This document outlines the development history of our RideShare application, tracking all changes from initial commit to the latest version. The application has evolved from a basic framework to a fully-functional ride-sharing platform with separate interfaces for passengers, drivers, and administrators.

The project follows semantic versioning (Major.Minor) practices, with each version representing significant feature additions, improvements, or bug fixes. This changelog serves as both documentation and a project timeline, providing transparency into our development process.

## Development Timeline Overview

The development process began in mid-February 2025 with the initial project setup and quickly progressed through core functionality implementation:

- **Feb 16-20**: Initial project setup and basic user authentication
- **Feb 21-25**: Dashboard UI development and security implementation
- **Feb 26-Mar 1**: Booking system development and admin management features
- **Mar 2-8**: Driver functionality, vehicle management, and billing systems
- **Mar 9-13**: User experience improvements, profile pages, and help sections

## Detailed Version History

| Version | Date | Description | Commit Message |
|---------|------|-------------|----------------|
| v1.0 | 16.02.2025 | Initial commit of the project | "initial commit" |
| v1.1 | 17.02.2025 | Adding signup function | "Added Signup Function" |
| v1.3 | 20.02.2025 | Adding Login Feature for users | "Added login feature" |
| v1.4 | 23.02.2025 | Adding Admin Dashboard UI | "Designed admin dashboard UI" |
| v1.5 | 23.02.2025 | Adding Passenger Dashboard UI | "Designed Passenger dashboard UI" |
| v1.6 | 23.02.2025 | Adding Driver Dashboard UI | "Added Driver Dashboard UI" |
| v1.7 | 23.02.2025 | Edited Driver Signup | "Edited Driver Signup" |
| v1.8 | 23.02.2025 | Added validations for Driver Registration | "Added validations for Driver Registration" |
| v1.9 | 23.02.2025 | Added Validations for Passenger Registration | "Added Validations for Passenger Registration" |
| v2.0 | 23.02.2025 | Added Validations for Admin Registration | "Added Validations for Admin Registration" |
| v2.1 | 23.02.2025 | Hashing password for Admin Registration | "added hashed password for admin registration" |
| v2.2 | 24.02.2025 | Hashing Password added for Driver Registration and Login | "Added Password hashing for Driver Registration and LogIn" |
| v2.1 | 24.02.2025 | Hashing Password added for Passenger Registration and Login | "Added Password hashing for Passenger Registration and LogIn" |
| v2.2 | 24.02.2025 | Hashing Password added for Admin Login | "Added Password hashing for Admin Login" |
| v2.3 | 24.02.2025 | Added unique username verification for Admin | "Added unique username verification for Admin" |
| v2.4 | 24.02.2025 | Added unique email, phone number, license number verification for Driver | "Added unique verifications for Driver" |
| v2.5 | 24.02.2025 | Added unique email, phone number, NIC verification for Driver | "Added unique verifications for Passenger" |
| v2.6 | 25.02.2025 | Added UI for Cab Booking for Passengers | "Added UI for Passenger Cab booking" |
| v2.7 | 25.02.2025 | Added session storage management for driver and passenger login | "Added session storing for passenger and driver" |
| v2.8 | 25.02.2025 | Added backend for saving booking data to database | "Added backend servlet and classes for Booking UI" |
| v2.9 | 26.02.2025 | Added UI for Admin Booking Management System | "UI added for admin to manage and assign booking to drivers" |
| v3.0 | 26.02.2025 | Added some UI elements in Admin Booking Management System | "Modified UI in admin booking management system" |
| v3.1 | 28.02.2025 | Added logics to booking management system of admin | "Added functios to UI in admin booking management system" |
| v3.2 | 01.03.2025 | Added some minor features to admin booking management | "added minor feautures in admin booking management" |
| v3.3 | 01.03.2025 | Online/Offline Function for Driver | "added online/offline function for driver" |
| v3.4 | 02.03.2025 | UI for Driver Ride Management | "added UI for driver ride management" |
| v3.5 | 02.03.2025 | Functionalities for Driver Ride Management | "added functionalities to driver ride management system" |
| v3.6 | 02.03.2025 | Added JS for better readability | "Added JS for better readability and removed comments" |
| v3.7 | 03.03.2025 | Added UI for Vehicle Management | "added UI for admin vehicle management" |
| v3.8 | 03.03.2025 | Implemented admin functionalities for managing vehicles | "added functionalities for admin vehicle management" |
| v3.9 | 04.03.2025 | Added passenger billing UI | "added UI passenger billing" |
| v4.0 | 04.03.2025 | CSS file added for passenger billing UI | "added css file for UI passenger billing" |
| v4.1 | 04.03.2025 | Modified the css file of passenger billing | "Modified the css file of passenger billing" |
| v4.2 | 05.03.2025 | Added functionalities to passenger billing | "added functionalities for passenger billing" |
| v4.3 | 05.03.2025 | Added UI for passenger booking history | "UI added for passenger booking history" |
| v4.4 | 06.03.2025 | Added functionalities for passenger booking history | "Functionalities added for passenger booking history" |
| v4.5 | 08.03.2025 | Added basic functions to driver earnings | "Some of the Functionalities added for driver earnings page" |
| v4.6 | 08.03.2025 | Added UI for Admin's wallet page | "Added UI for Admin Wallet" |
| v4.7 | 08.03.2025 | Added base function to admin wallet page | "added basic functions for admin wallet page" |
| v4.8 | 09.03.2025 | Added UI for driver's ride history page | "added UI for driver ride history" |
| v4.9 | 10.03.2025 | Added Functionalities to driver's ride history page | "added functionalities for driver ride history" |
| v5.0 | 10.03.2025 | Added more Functionalities to some User Interfaces | "added more functionalities for some UIs" |
| v5.1 | 12.03.2025 | Added test files from development to regression | "Added test files from development to regression" |
| v5.2 | 13.03.2025 | Fixed some errors | "some of the minor errors fixed" |
| v5.3 | 13.03.2025 | Added profile page for passenger | "Added UI for Passenger profile" |
| v5.4 | 13.03.2025 | Added logout function and help page | "Added logout function and help page for passenger" |
| v5.6 | 13.03.2025 | Added profile for driver | "Added driver profile" |
| v5.7 | 13.03.2025 | Added logout for driver | "Added logout function for driver" |
| v5.8 | 13.03.2025 | Added driver help page | "Added help page for drivers" |
| v5.9 | 13.03.2025 | Improved the passenger dashboard | "Improved passenger dashboard etc" |
| v6.0 | 13.03.2025 | Improved the driver dashboard | "Improved driver dashboard" |

## Major Feature Milestones

### User Authentication & Security (v1.0 - v2.5)
- User registration and login functionality for all user types
- Password hashing implementation for secure authentication
- Unique identifier verification for all user types

### Booking System (v2.6 - v3.2)
- Passenger cab booking interface
- Admin booking management system
- Backend database integration for ride data

### Driver Management (v3.3 - v3.6)
- Driver online/offline status functionality
- Ride management interface for drivers
- UI improvements for better user experience

### Vehicle & Billing (v3.7 - v4.7)
- Vehicle management system for administrators
- Passenger billing interface and functionality
- Admin wallet implementation
- Driver earnings tracking

### User Experience & History (v4.8 - v6.0)
- Ride history tracking for both passengers and drivers
- User profiles for all user types
- Help pages and documentation
- Dashboard improvements
- Testing and bug fixes

## Notes on Versioning

There are some inconsistencies in the versioning sequence, particularly around versions 2.1 and 2.2 which appear twice. This should be reviewed and corrected in future documentation updates to maintain proper version control.

## Current Status

As of March 13, 2025, the application has reached version 6.0 with all core functionalities implemented. Current focus is on improving user experience and resolving minor issues.
