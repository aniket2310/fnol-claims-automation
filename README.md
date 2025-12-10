# FNOL Claims Automation System

A Spring Boot application that automates First Notice of Loss (FNOL) claim processing using PDF extraction, field parsing, validation, and intelligent claim routing.

## Features

- PDF / Text Extraction using Apache PDFBox
- Field Extraction (policy number, incident date, location, etc.)
- Missing Field Validation
- Auto Routing Engine
  - SPECIALIST_QUEUE
  - FAST_TRACK
  - INVESTIGATION
  - MANUAL_REVIEW
- Save & Retrieve Claims from database
- Swagger API Documentation
- Postman Test Support

## Main APIs

| Method | Endpoint                     | Description                         |
|--------|------------------------------|-------------------------------------|
| POST   | /api/claims/extract-text     | Upload file & extract raw text      |
| POST   | /api/claims/analyze          | Extract fields + validate + route   |
| POST   | /api/claims/save             | Save analyzed claim                 |
| GET    | /api/claims                  | List all saved claims               |
| GET    | /api/claims/{id}             | Get single claim                    |

## Technologies Used

- Java 17  
- Spring Boot  
- Apache PDFBox  
- Spring Data JPA  
- MySQL  
- Maven  
- Swagger (Springdoc OpenAPI)

## How to Start & Run the FNOL Claims Automation Project

Follow these steps from download → setup → run → test.

### 1. Clone the Project

```bash
git clone https://github.com/<your-username>/fnol-claims-agent.git
cd fnol-claims-agent
```

### 2. Open the Project in IntelliJ / VS Code

- Open the folder: `fnol-claims-agent`
- Let the IDE download all Maven dependencies

### 3. Configure Application Properties

File:

```
src/main/resources/application.properties
```

Example (H2 Database):

```properties
spring.datasource.url=jdbc:h2:mem:claimsdb
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

Or from IntelliJ:

- Run → Application.java → Run

### 6. Access Swagger Documentation

Open:

```
http://localhost:8080/swagger-ui.html
```

### 7. Test APIs Using Postman

#### Extract Text

```
POST /api/claims/extract-text
```

Form-data:

```
file: <your-file>
```

#### Analyze Claim

```
POST /api/claims/analyze
```

Form-data:

```
file: <your-file>
```

#### Save Claim

```
POST /api/claims/save
```

Body (raw JSON):

```json
{
  "policyNumber": "12345",
  "incidentDate": "2024-01-01",
  "route": "FAST_TRACK"
}
```

#### Get All Claims

```
GET /api/claims
```

#### Get Claim by ID

```
GET /api/claims/{id}
```

### 8. Optional: Run with MySQL

Update properties:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fnol
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 9. Stop the Server

Press:

```
CTRL + C
```

Or stop from IDE.

## Author

Aniket Bodhe  
Java | Spring Boot | Angular | Full Stack Developer
