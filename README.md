FNOL Claims Automation System

A Spring Boot application that automates First Notice of Loss (FNOL) claim processing using PDF extraction, field parsing, validation, and intelligent claim routing.

🚀 Features

📄 PDF / Text Extraction using Apache PDFBox

🔍 Field Extraction (policy number, incident date, location, etc.)

✔️ Missing Field Validation

🤖 Auto Routing Engine

SPECIALIST_QUEUE

FAST_TRACK

INVESTIGATION

MANUAL_REVIEW

💾 Save & Retrieve Claims from database

📚 Swagger API Documentation

🔄 Postman Test Support

📁 Main APIs
Method	Endpoint	Description
POST	/api/claims/extract-text	Upload file & extract raw text
POST	/api/claims/analyze	Extract fields + validate + route
POST	/api/claims/save	Save analyzed claim
GET	/api/claims	List all saved claims
GET	/api/claims/{id}	Get single claim
🏗️ Technologies Used

Java 17

Spring Boot

Apache PDFBox

Spring Data JPA

MySQL

Maven

Swagger (Springdoc OpenAPI)






🚀 How to Start & Run the FNOL Claims Automation Project

Follow these steps from download → setup → run → test.

1️⃣ Clone the Project
git clone https://github.com/<your-username>/fnol-claims-agent.git
cd fnol-claims-agent

2️⃣ Open the Project in IntelliJ / VS Code

Open folder: fnol-claims-agent

Let the IDE download all Maven dependencies

3️⃣ Configure Application Properties

Check file:

src/main/resources/application.properties


Example (H2 Database):

spring.datasource.url=jdbc:h2:mem:claimsdb
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

4️⃣ Build the Project
mvn clean install

5️⃣ Run the Application
mvn spring-boot:run


or from IntelliJ:

Run → Application.java → Run

6️⃣ Access Swagger Documentation

Open browser:

http://localhost:8080/swagger-ui.html

7️⃣ Test APIs Using Postman
1. Extract Text
POST /api/claims/extract-text
Upload file → form-data → key: file

2. Analyze Claim
POST /api/claims/analyze
Upload file → form-data → key: file

3. Save Claim
POST /api/claims/save
Body → raw JSON (analysis result)

4. Get Claims
GET /api/claims

5. Get Claim by ID
GET /api/claims/{id}

8️⃣ (Optional) Run With MySQL

Update:

spring.datasource.url=jdbc:mysql://localhost:3306/fnol
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

9️⃣ Stop the Server

Press:

CTRL + C


or stop from IDE.

🎉 Your FNOL Claims System is now running successfully!

👨‍💻 Author

Aniket Bodhe
Java | Spring Boot | Angular | Full Stack Developer
