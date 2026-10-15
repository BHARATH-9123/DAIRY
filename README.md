# M-DAIRY
> **"From Farm To Home"** — Modern, Complete Dairy Management System

---

## Architecture & Technology Stack
- **Frontend**: React 18, Vite, React Router 6, Axios, Lucide React, Custom iOS-inspired Glassmorphism CSS Design System.
- **Backend**: Java 17+, Spring Boot 3.3.4 (Spring Web, Spring Data JPA, Hibernate, Bean Validation, DevTools), Maven.
- **Database**: MySQL 8.0 (`m_dairy` database).
- **Access**: Instant unauthenticated dashboard access on `http://localhost:5173` without login walls.

---

## Project Structure
```
DIARY_WEB/
├── frontend/                   # React + Vite Frontend
│   ├── src/
│   │   ├── components/         # Glassmorphism UI components (Sidebar, Header, Modal, Toast, etc.)
│   │   ├── pages/              # Overview, Customers, Vendors, Attendance, Billing, Reports, Settings
│   │   ├── services/           # Axios API services
│   │   ├── styles/             # Variables, global styles, and component CSS
│   │   └── utils/              # INR currency formatters, CSV export engine
│   ├── package.json
│   ├── vite.config.js
│   └── index.html
├── backend/                    # Spring Boot Backend
│   ├── src/main/java/com/mdairy/
│   │   ├── config/             # CORS and Web MVC setup
│   │   ├── controller/         # REST Controllers
│   │   ├── dto/                # Request & Response DTOs
│   │   ├── entity/             # JPA Entities
│   │   ├── exception/          # Centralized Exception Handler
│   │   ├── repository/         # Spring Data JPA Repositories
│   │   └── service/            # Core business logic & implementations
│   ├── src/main/resources/     # application.properties (MySQL datasource)
│   ├── src/test/java/          # Automated regression & integration test suite
│   └── pom.xml
├── database/
│   └── schema.sql              # MySQL DDL script with foreign keys & indexes
├── docs/                       # Architecture, API & Schema documentation
└── README.md
```

---

## Step-by-Step Setup & Execution Guide (Windows)

### 1. Prerequisites & Required Tools
Ensure the following are installed:
- **Java Development Kit (JDK 17 or higher)** (`java -version`)
- **Apache Maven 3.8+** (`mvn -v`)
- **Node.js (v18+) & npm** (`node -v`, `npm -v`)
- **MySQL Server 8.0** running on port 3306
- **Spring Tools Suite (STS)** or IntelliJ IDEA for Java development
- **Visual Studio Code** for React development

---

### 2. Create the MySQL Database
Open Windows Command Prompt or PowerShell:
```powershell
mysql -u root -p
```
Run:
```sql
CREATE DATABASE IF NOT EXISTS m_dairy CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;
```
Import the provided schema:
```powershell
Get-Content "database\schema.sql" | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p --default-character-set=utf8mb4
```

---

### 3. Configure Database Credentials
Open `backend/src/main/resources/application.properties`. If your MySQL credentials differ from `root`/`root`, set the following environment variables or update the file directly:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/m_dairy?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Kolkata
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

---

### 4. Import the Backend into Spring Tools Suite (STS)
1. Open **Spring Tools Suite (STS)**.
2. Select **File** > **Import...**
3. Choose **Maven** > **Existing Maven Projects** and click **Next**.
4. In the **Root Directory**, browse to `DIARY_WEB/backend`.
5. Ensure `pom.xml` is checked and click **Finish**.

---

### 5. Download Maven Dependencies
STS will automatically resolve and download dependencies from Maven Central.
You can also manually build from terminal:
```powershell
cd backend
mvn clean install -DskipTests
```

---

### 6. Run the Spring Boot Application
**In STS**:
- Right-click the project `m-dairy-server` > **Run As** > **Spring Boot App**.

**From Command Line**:
```powershell
cd backend
mvn spring-boot:run
```
The backend will boot up at `http://localhost:8080`.

---

### 7. Open the Frontend in VS Code
Open Visual Studio Code:
```powershell
code frontend
```

---

### 8. Install Frontend Dependencies
In the VS Code terminal:
```powershell
cd frontend
npm install
```

---

### 9. Start the React Development Server
```powershell
npm run dev
```
The React application will be accessible at:
👉 **`http://localhost:5173`**

The dashboard will open directly without requiring any login!

---

### 10. Verify Frontend-Backend Communication
- Open `http://localhost:5173`.
- The dashboard status indicator in the sidebar footer will display `● Connected`.
- Dashboard summary cards will query live data from `http://localhost:8080/api/dashboard/summary`.

---

### 11. Run the Tests
Execute the comprehensive automated test suite (including the customer-attendance billing recalculation regression test):
```powershell
cd backend
mvn test
```
**Regression Scenario Verified**:
- Cow selling rate: ₹100/L, Buffalo: ₹200/L.
- Day 1: 5L cow + 2L buffalo = ₹900.
- Day 2: 3L cow + 1L buffalo = ₹500. Initial total: 11L = ₹1,400.
- Updating Day 2 cow quantity to 4L recalculates draft bill to: 12L = ₹1,500.
- Valid recorded payments are safely reconciled and issued invoices are protected from overwrite.

---

### 12. Build Frontend for Production
```powershell
cd frontend
npm run build
```
The production bundle will be generated in `frontend/dist`.

---

### 13. Troubleshooting Common Issues
- **MySQL Access Denied (Error 1045)**:
  Ensure password in `backend/src/main/resources/application.properties` matches your local MySQL `root` password.
- **Port 8080 Already in Use**:
  Terminate any conflicting process or set `server.port=8081` in `application.properties` and update `VITE_API_URL` in `frontend/.env`.
- **CORS Errors**:
  The backend is preconfigured in `WebConfig.java` to allow origins `http://localhost:5173` and `http://127.0.0.1:5173`.
