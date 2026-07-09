# Employee Management System

A Spring Boot REST API representing three types of employees — **Hourly**, **Salaried**, and **Manager** — with vacation accrual and work-day tracking.

---

## Tech Stack

| Tool | Version |
|------|---------|
| Java | 21 |
| Spring Boot | 3.2.5 |
| Maven | 3.x |
| Port | 8080 |

---

## How to Run

### Prerequisites
- Java 21 installed (`java -version`)
- Maven installed (`mvn -version`) — if not, run: `sudo apt-get install -y maven`

### Step 1 — Build
```bash
cd /home/rambabu/Desktop/javatest
mvn clean package -DskipTests
```
You should see `BUILD SUCCESS` at the end.

### Step 2 — Start the Server
```bash
java -jar target/employee-management-1.0.0.jar
```
The server starts on **http://localhost:8080**

Look for this line to confirm it's running:
```
Started EmployeeManagementApplication in X seconds
```

### Stop the Server
Press `Ctrl + C` in the terminal.

---

## On Startup — Auto-Created Employees

When the app starts, **30 employees** are automatically created in memory:

| IDs   | Type     | Vacation Days / Year | Accrual Rate (per day worked) |
|-------|----------|----------------------|-------------------------------|
| 1–10  | Hourly   | 10 days              | 10 / 260 ≈ 0.03846            |
| 11–20 | Salaried | 15 days              | 15 / 260 ≈ 0.05769            |
| 21–30 | Manager  | 30 days              | 30 / 260 ≈ 0.11538            |

> A work year = **260 workdays**. Vacation starts at **0** and cannot go negative.

---

## API Endpoints

### Base URL
```
http://localhost:8080/api/employees
```

---

### 1. Get All Employees
```
GET http://localhost:8080/api/employees
```
Returns all 30 employees sorted by ID.

---

### 2. Get Summary (count by type)
```
GET http://localhost:8080/api/employees/summary
```
**Sample Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "HOURLY": 10,
    "SALARIED": 10,
    "MANAGER": 10
  }
}
```

---

### 3. Get Employees by Type
```
GET http://localhost:8080/api/employees/type/HOURLY
GET http://localhost:8080/api/employees/type/SALARIED
GET http://localhost:8080/api/employees/type/MANAGER
```

---

### 4. Get Employee by ID
```
GET http://localhost:8080/api/employees/{id}
```
**Example:**
```
GET http://localhost:8080/api/employees/1
```
**Sample Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "employeeType": "HOURLY",
    "id": 1,
    "name": "Hourly Employee 1",
    "totalDaysWorked": 0,
    "vacationDaysAccumulated": 0.0,
    "remainingWorkdays": 260,
    "vacationDaysPerYear": 10
  }
}
```

---

### 5. Record Days Worked — `POST /{id}/work`
```
POST http://localhost:8080/api/employees/{id}/work
```

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "days": 130
}
```

**Rules:**
- `days` must be between `0` and `260`
- Cumulative days worked cannot exceed `260` in a work year

**Sample Response (Hourly employee, 130 days worked):**
```json
{
  "success": true,
  "message": "Recorded 130 day(s) worked. Vacation balance: 5.0000 day(s).",
  "data": {
    "employeeType": "HOURLY",
    "id": 1,
    "vacationDaysAccumulated": 5.0,
    "totalDaysWorked": 130
  }
}
```

---

### 6. Take Vacation — `POST /{id}/vacation`
```
POST http://localhost:8080/api/employees/{id}/vacation
```

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "days": 2.5
}
```

**Rules:**
- `days` must be `≥ 0`
- Cannot exceed the available vacation balance

**Sample Response:**
```json
{
  "success": true,
  "message": "Took 2.50 vacation day(s). Remaining balance: 2.5000 day(s).",
  "data": {
    "employeeType": "HOURLY",
    "id": 1,
    "vacationDaysAccumulated": 2.5,
    "totalDaysWorked": 130
  }
}
```

---

## Error Responses

All errors return the same envelope with `"success": false`.

### Employee Not Found (404)
```
GET http://localhost:8080/api/employees/999
```
```json
{
  "success": false,
  "message": "No employee found with id: 999",
  "data": null
}
```

### Work Days Exceed Annual Limit (400)
```json
{ "days": 261 }
```
```json
{
  "success": false,
  "message": "Days worked must be between 0 and 260. Provided: 261",
  "data": null
}
```

### Insufficient Vacation Balance (400)
```json
{ "days": 999 }
```
```json
{
  "success": false,
  "message": "Cannot take 999.00 vacation day(s). Only 5.00 day(s) available.",
  "data": null
}
```

---

## Postman Quick Reference

| # | Method | URL | Body |
|---|--------|-----|------|
| 1 | GET | `http://localhost:8080/api/employees` | — |
| 2 | GET | `http://localhost:8080/api/employees/summary` | — |
| 3 | GET | `http://localhost:8080/api/employees/type/HOURLY` | — |
| 4 | GET | `http://localhost:8080/api/employees/type/SALARIED` | — |
| 5 | GET | `http://localhost:8080/api/employees/type/MANAGER` | — |
| 6 | GET | `http://localhost:8080/api/employees/1` | — |
| 7 | POST | `http://localhost:8080/api/employees/1/work` | `{"days": 130}` |
| 8 | POST | `http://localhost:8080/api/employees/1/vacation` | `{"days": 2.5}` |

> For POST requests, set Header: `Content-Type: application/json` and Body: **raw → JSON**

---

## Project Structure

```
src/
├── main/java/com/employeetest/
│   ├── EmployeeManagementApplication.java   # Entry point
│   ├── model/
│   │   ├── Employee.java                    # Abstract base class (all business rules)
│   │   ├── HourlyEmployee.java              # 10 vacation days/year
│   │   ├── SalariedEmployee.java            # 15 vacation days/year
│   │   └── Manager.java                     # 30 vacation days/year (extends Salaried)
│   ├── service/
│   │   └── EmployeeService.java             # In-memory store + startup initialization
│   ├── controller/
│   │   └── EmployeeController.java          # REST API (6 endpoints)
│   ├── dto/
│   │   ├── WorkRequest.java
│   │   ├── VacationRequest.java
│   │   └── ApiResponse.java
│   └── exception/
│       └── GlobalExceptionHandler.java      # Centralized error handling
└── test/java/com/employeetest/model/
    └── EmployeeTest.java                    # 14 unit tests
```
