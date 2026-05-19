# Bookify

Full-stack library management system built with Spring Boot and MySQL on the backend, and Vue 3 with TypeScript and Tailwind CSS on the frontend.

---

## Technologies Used

- **Backend Framework:** Spring Boot (Spring Security, Spring Data JPA, Lombok)
- **Database:** MySQL
- **Frontend Framework:** Vue 3 (Composition API / `<script setup>`)
- **Language:** TypeScript
- **State Management:** Pinia
- **Styles:** Tailwind CSS

---

## Key Features

- **Authentication & Roles:** Security system based on Basic Auth featuring two access levels (`ADMIN` for full control and `USER` for standard interactions).
- **Assignment Control:** Dynamic loan workflow that allows assigning, self-assigning, or releasing books from the inventory in real time.
- **Multimedia Management:** Support for uploading images on the frontend and efficiently storing thumbnails as data URI/base64 in the database.
- **Catalog Pagination:** Optimized user interface that segments records into blocks of 6 elements to maximize performance.
- **Data Seeder:** Automatic injection of 32 initial books with server-generated SVG covers for immediate testing.
- **Data Export:** Direct download of structured reports in `books.csv` format, restricted to administrators.

---

## System Requirements

- Java 17 or higher
- Maven 3.9+
- Node.js 18+
- MySQL Server running locally

---

## Installation and Setup

Follow these steps to clone the project and run both environments locally:

### 1. Clone the project

```bash
git clone [https://github.com/loreennzzo016/Bookify.git](https://github.com/loreennzzo016/Bookify.git)
cd Bookify

```

### 2. Configure and Start the Backend

Make sure your MySQL server is running. The database `online_library` will be created automatically. You can check the credentials in `backend/src/main/resources/application.properties` (defaults to user `root` with no password).

```bash
cd backend
mvn spring-boot:run

```

_API running at `http://localhost:8080/api_`

### 3. Configure and Start the Frontend

```bash
cd ../frontend
npm install
npm run dev

```

_Web application running at `http://localhost:5173_`

---

## Contributing

If you want to improve the project, fix bugs, or add new features:

1. Fork this repository.
2. Create a branch for your feature (`git checkout -b feature/NewFeature`).
3. Commit your changes (`git commit -m 'Add: New functionality'`).
4. Push the branch (`git push origin feature/NewFeature`).
5. Open a Pull Request.

---

Developed by [loreennzzo016](https://github.com/loreennzzo016).
