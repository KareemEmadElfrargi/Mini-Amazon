# 🛒 Mini-Amazon Backend API

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0-green)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue)
![Security](https://img.shields.io/badge/Security-JWT-red)


## 📖 Overview
**Mini-Amazon** is a robust RESTful API designed for an e-commerce platform. It manages the core functionalities of an online store, including user authentication, product catalog management, shopping cart operations, and order processing.

This project demonstrates a clean **Layered Architecture** and industry standard practices for building scalable backend systems using **Spring Boot 4**.

---

## 🚀 Key Features

* **Secure Authentication:** Stateless authentication using **JWT (JSON Web Tokens)** with Role-Based Access Control (Admin vs. Customer).
* **Product Management:** Efficient browsing with **Pagination** and **Sorting**.
* **Smart Cart System:**
    * Users can add/remove items.
    * Real-time stock validation before adding items.
* **Order Processing:**
    * Atomic Checkout process.
    * **Automatic Stock Deduction:** Reduces product quantity upon successful order placement to prevent overselling.
* **Robust Error Handling:** Global Exception Handling using `@ControllerAdvice` to return standardized JSON error responses.
* **Data Validation:** Strong input validation using Hibernate Validator to ensure data integrity.

---

## 🛠️ Technology Stack

* **Core:** Java 17, Spring Boot 4.x
* **Database:** PostgreSQL
* **ORM:** Spring Data JPA (Hibernate)
* **Security:** Spring Security, JWT
* **Utilities:** Lombok, Maven

---

## 🏗️ Architecture & Design

The project follows a **Layered Architecture** to ensure separation of concerns:

1.  **Controller Layer:** Handles HTTP requests and maps them to service methods. returns **DTOs** (Data Transfer Objects) instead of Entities to hide internal database structure.
2.  **Service Layer:** Contains the business logic (e.g., calculating totals, validating stock, processing payments).
3.  **Repository Layer:** Abstract interface for Database operations using Spring Data JPA.
4.  **Domain Layer:** The core Database Entities.

### Database Schema (ERD Concepts)
* **User** `1:1` **Cart**
* **Cart** `1:N` **CartItem**
* **User** `1:N` **Order**
* **Category** `1:N` **Product**

---

## ⚙️ Setup & Installation

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/YourUsername/mini-amazon-api.git](https://github.com/KareemEmadElfrargi/mini-amazon-api.git)
    cd mini-amazon-api
    ```

2.  **Configure Database:**
    * Create a PostgreSQL database named `mini_amazon_db`.
    * Update `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/mini_amazon_db
    spring.datasource.username= your user name
    spring.datasource.password= your password
    
    # JWT Secret Key
    application.security.jwt.secret-key= YOUR_SUPER_SECRET_KEY_HERE

    NOTE : this configure in my project i know this way not secure but i remain this file for your test app
    ```

3.  **Run the Application:**
    ```bash
    mvn spring-boot:run
    ```

4.  **Test the API:**
    * The app will run at `http://localhost:8080`

---

## 🔌 API Endpoints (Examples)

### 🔐 Authentication
* `POST /api/auth/register` - Create a new user.
* `POST /api/auth/login` - Login and receive JWT Token.

### 📦 Products
* `GET /api/products?page=0&size=10` - Get all products (Paginated).
* `POST /api/products` - Add new product (Admin only).

### 🛒 Cart
* `POST /api/cart/items` - Add item to cart.
* `DELETE /api/cart/items/{itemId}` - Remove item.

### 🧾 Orders
* `POST /api/orders/checkout` - Convert Cart to Order and deduct stock.

---

## 🔮 Future Improvements
* **Payment Gateway Integration:** (Stripe or PayPal).
* **Email Notifications:** Send order confirmation emails.
* **Docker Support:** Containerize the application for easy deployment.
* **Redis Caching:** Implement caching for frequently accessed products.

---



## 👨‍💻 Author
**Kareem Emad** Software Engineer | Backend Developer  
[Linked Profile](https://www.linkedin.com/in/kareememad0101/)
