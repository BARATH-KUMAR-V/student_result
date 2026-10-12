# Student Result Portal 🎓

A clean, responsive, and lightweight Student Result Management Portal built using **Java Spring Boot**, **H2 Database** (with ready-to-use **MySQL** integration), and a **Jenkins CI/CD Pipeline**.

---

## 🚀 Features

- **Public Student Result Search:** Search results instantly by Roll Number (e.g., `STU-101`, `STU-102`).
- **Verified Mark Sheet Card:** Displays subject-wise scores, total (out of 500), percentage, grade, and status (`PASS` / `FAIL`). Includes printable / PDF-export support.
- **Admin Dashboard:**
  - Real-time aggregate statistics (Total Students, Passed, Failed, Pass Rate, Class Average, Highest Marks).
  - Add / Edit / Delete student records with validation.
  - Live in-modal calculation of total marks, percentage, and grade prediction as marks are entered.
- **Zero Configuration Database:** Pre-configured with in-memory **H2 Database** and seeded sample data. Easily toggle to **MySQL** anytime.
- **CI/CD Automation:** Includes a complete **Jenkinsfile** implementing `Checkout`, `Build`, `Test`, `Package`, and `Archive` stages.

---

## 🛠️ Technology Stack

- **Backend:** Java 21+ / Spring Boot 3.3.4
- **ORM & Database:** Spring Data JPA, Hibernate, H2 Database (In-Memory), MySQL Connector
- **Testing:** JUnit 5, Mockito, Spring Boot Test / MockMvc
- **Frontend:** HTML5, CSS3 (Modern Flexbox/Grid, Responsive), Vanilla JavaScript
- **Build & CI/CD:** Apache Maven, Jenkins Declarative Pipeline

---

## ⚡ Quick Start

### 1. Run with Maven
```bash
mvn clean spring-boot:run
```

### 2. Or Build & Run JAR
```bash
mvn clean package
java -jar target/student-result-portal-1.0.0.jar
```

### 3. Open in Browser
- **Portal UI:** [http://localhost:8081](http://localhost:8081)
- **H2 Web Console:** [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
  - JDBC URL: `jdbc:h2:mem:studentdb`
  - Username: `sa`
  - Password: *(leave blank)*

---

## 🗄️ Database Options

### Default: H2 (Active)
H2 runs automatically in memory when you start the application. 6 sample students (`STU-101` to `STU-106`) are seeded on startup.

### Switching to MySQL
1. Create a MySQL database:
   ```sql
   CREATE DATABASE studentdb;
   ```
2. In `src/main/resources/application.properties`, uncomment the MySQL lines:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/studentdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
   spring.jpa.hibernate.ddl-auto=update
   ```

---

## 🔄 Jenkins CI/CD Pipeline

The repository includes a ready-to-run declarative `Jenkinsfile` with 5 automated stages:

| Stage | Command / Step | Purpose |
| :--- | :--- | :--- |
| **1. Checkout** | `checkout scm` | Pulls the repository from Git |
| **2. Build** | `mvn clean compile` | Compiles Java code & checks syntax |
| **3. Test** | `mvn test` | Runs unit tests & collects JUnit reports |
| **4. Package** | `mvn package -DskipTests`| Generates `target/*.jar` |
| **5. Archive** | `archiveArtifacts 'target/*.jar'` | Saves build artifact for deployment |

### Setting up Jenkins Job:
1. In Jenkins, create a **New Item** → select **Pipeline**.
2. Under **Pipeline Definition**, select **Pipeline script from SCM**.
3. Choose **Git** and enter Repository URL: `https://github.com/BARATH-KUMAR-V/student_result.git`.
4. Script Path: `Jenkinsfile`.
5. Click **Save** & **Build Now**.

---

## 📡 REST API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/results` | Get all student results or search by `?search=` |
| `GET` | `/api/results/{rollNumber}` | Get result for specific roll number |
| `POST` | `/api/results` | Create new student result |
| `PUT` | `/api/results/{id}` | Update existing student result |
| `DELETE` | `/api/results/{id}` | Delete student result |
| `GET` | `/api/results/stats` | Get overview metrics |
