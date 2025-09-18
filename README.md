# Library Management System (Java + Hibernate)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Setup & Installation](#setup--installation)
- [Usage](#usage)
- [Database Schema](#database-schema)
- [Future Improvements](#future-improvements)
- [Contributing](#contributing)
- [License](#license)
- [Author](#author)

---
## Recent Updates



## Overview
This is a **console-based Library Management System** that supports multiple user roles: **Admin**, **Librarian**, and **Member**.  
It allows managing books, book copies, users, transactions, and reservations using **Java** and **Hibernate ORM** for persistence with **MySQL**.

---

## Features

### Admin
- Create, update, and delete users.
- Add, update, and delete books.
- View all transactions and fines.
- Role-based access control.
- Added support for `maxReservationsPerUser` per book.
- Improved transaction handling (issue, return, fines, auto-reservation fulfillment).
- Updated BookDAO, ReservationService, and TransactionService.

### Librarian
- Issue and return books.
- Manage active transactions for members.
- Check availability of book copies.

### Member
- Search books by title or author.
- Borrow and return books.
- View own transactions and fines.
- Manage reservations.

---

## Technology Stack
- **Language:** Java 17+
- **ORM:** Hibernate
- **Database:** MySQL
- **Build Tool:** Maven
- **Other Libraries:** JPA, JDBC, Lombok (optional)

---

## Setup & Installation
1. **Clone the repository:**
```bash
git clone https://github.com/GowthamChiriki/Library-Management-System-Java-Hibernate.git
cd Library-Management-System-Java-Hibernate
```
2. Setup MySQL Database:
- Create a database library_db.
- Update the HibernateUtil.java file with your database credentials.

3. Run the application:
- Open LibraryApp.java in your IDE.
- Run as a Java Application.

4. Default Admin Credentials:
- Email: gowthamsai167@gmail.com
- Password: Gowtham@2929

## Usage:
Console-driven menu system:
- Admin: Manage users and books, view transactions.
- Librarian: Issue/return books, view active transactions.
- Member: Search books, borrow/return, check fines, manage reservations.

## Database Schema
- Users: id, name, email, password, role
- Books: id, title, author, isbn, category, copiesCount
- BookCopies: id, book_id, status (AVAILABLE/ISSUED)
- Transactions: id, user_id, book_copy_id, issue_date, due_date, return_date, fine_amount, status (ACTIVE/RETURNED)
- Reservations: id, user_id, book_id, reservation_date, status

## Future Improvements
- CSV import/export for bulk book and user management.
- GUI or Web interface with Spring Boot + Thymeleaf / React.
- Email notifications for due dates and reservations.
- Advanced reporting dashboard for fines, popular books, and overdue items.
- API integration for book metadata (ISBN, author, category) fetching.

## Contributing
- Fork the repository.
- Create a feature branch (git checkout -b feature/YourFeature).
- Commit your changes (git commit -m 'Add new feature').
- Push to the branch (git push origin feature/YourFeature).
- Open a Pull Request.

## License
- This project is licensed under the MIT License.

## Author
- Gowtham Sai Chiriki
- Email: gowthamsai167@gmail.com
- GitHub: https://github.com/GowthamChiriki


## MIT License

## Copyright (c) 2025 Gowtham Chiriki

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

