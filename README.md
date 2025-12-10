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

▶️ How It Works

User uploads a filled FNOL form (PDF)

System extracts raw text

Claim fields are auto-detected

Missing fields validated

Routing logic determines correct processing queue

Claim is stored for backend processing

📘 Swagger URL
http://localhost:8080/swagger-ui.html

📦 Project Setup
git clone <repo-url>
cd fnol-claims-agent
mvn spring-boot:run

👨‍💻 Author

Aniket Bodhe
Java | Spring Boot | Angular | Full Stack Developer
