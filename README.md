# Habit Tracker API

It is a backend application for managing user habits and tracking progress through a point-based system.  
It provides authentication and authorization via **Keycloak (JWT)** and runs on **Spring Boot with PostgreSQL**.


**Build With:**
**Spring Boot, Keycloak (JWT), PostgreSQL, Docker, Insomnia**

**Technologies:**
- **Java 17**
- **Spring Boot 3**
- **Apache Maven 3.9.9**
- **Spring Security (JWT)**
- **Keycloak**
- **PostgreSQL**
- **Docker & Docker Compose**
- **JUnit & Mockito**
- **Insomnia for API testing**

---

## Key Features:
- **User authentication & authorization via Keycloak (JWT)**  
- **Custom Keycloak Event Listener** → Automatically assigns points upon user registration  
- **Managing habits & tracking progress**  
- **Automatic point allocation for completed habits**  
- **Rollback system for habit edits** → If a user updates a habit record (e.g., from **done** → **undone**), the system automatically **reverts the progress**:
  - Deducts points that were previously awarded
  - Restores the number of days required to master the habit  
- **Automated task status updates (Scheduler)** → If a user forgets to mark a habit after a set number of days (`application.yml` configurable):
  - The system automatically **marks the habit as "undone unchecked"**
  - **Subtracts points for undoing habit**
  - **Locks the record to prevent later edits**  
- **Global error handling** → The application has a centralized error-handling mechanism**
- **Docker-based environment for easy setup**


---

##  Authentication & Event System  
- Users authenticate via **Keycloak**, which issues JWT tokens used for API authorization.  
- A custom **Keycloak Event Listener Provider** is implemented in a separate module (`keycloak-listener`).  
- When a user registers, Keycloak **triggers an event** that notifies the main application, which then **automatically assigns starting points** to the user.  
  
---

## How to Run

### Clone repository

```sh
git clone https://github.com/m-stepien/habbit_tracker.git
cd habit_tracker
```

### Build packages

```sh
mvn -pl keycloak-listener package
```

### Run Keycloak and PostgreSQL using Docker Compose

```sh
docker-compose up --build -d
```

### Start the Spring Boot application:

```sh
mvn -pl core spring-boot:run
```

---

## API Requests (Insomnia)

**The request structure, headers, and responses can be found in Insomnia.**  
To import and use them, follow the steps below:

**Download Insomnia requests**: [`insomnia_requests.json`](./core/src/main/resources/insomnia-requests-export.json)
### How to Import API Requests in Insomnia?
1. Download the file above.  
2. Open **Insomnia** and go to `Application` → `Preferences` → `Data`.  
3. Click **"Import Data"** → **"From File"**.  
4. Select `insomnia_requests.json` and click **"Import"**.

---

## Next Steps 

Below are some potential future improvements:

**Implement frontend (React + TailwindCSS)**  
**Add a CI/CD pipeline (GitHub Actions or Jenkins)**  
**Improve test coverage (integration and e2e tests)** 
**Optimize database queries and implement indexing in PostgreSQL**  
**Expand API documentation and generate automatic OpenAPI specs** 

