# Rún - Cybersecurity Fundamentals Application

## Team 7

| Name | Student Number | Module |
|------|---------------|--------|
| Adam Durand | G00405974 | Scan History | Dashboard |
| Fionn Qualter | G00428202 | Link Checker, File Scanner |
| Nusrathara Hanif | G00415420 | Login, Email Scanner |
| Callum Stewart | G00432230 | Password Checker, Data Breach Checker |

## About

Rún (Irish for "secret/vault") is a desktop cybersecurity utility application. It provides tools to help users check URLs, scan files, analyse emails for phishing, test password strength, and check for data breaches. Built with Java Swing (frontend), Spring Boot (backend), and MySQL (database).

## Prerequisites

- Java 17 (JDK)
- Maven (added to PATH)
- MySQL 8

## Database Setup

1. Open MySQL Workbench and connect to your local server
2. Run the schema.sql file from the project root
3. Open backend/run-backend/src/main/resources/application.properties
4. Make sure the password matches your MySQL root password

Default credentials:
- Username: root
- Password: root1234
- Database: run_db
- Port: 3306

## How To Run

Open two terminals in VS Code:

Terminal 1 - Start the backend:
cd backend/run-backend
mvn spring-boot:run
Wait until you see "Started App" in the console.

Terminal 2 - Start the frontend:
cd frontend
mvn exec:java "-Dexec.mainClass=com.run.ui.RunApp"

The application window will open with a sidebar listing all available tools.

## Project Structure
run-project/
schema.sql
backend/run-backend/
src/main/java/com/run/
models/         - shared data models
dto/            - request/response objects
config/         - CORS and error handling
services/       - business logic for each scanner
controllers/    - REST API endpoints
repositories/   - database access
src/test/java/com/run/
src/main/resources/
application.properties
frontend/
src/main/java/com/run/ui/
RunApp.java         - app entry point
MainFrame.java      - main window with sidebar
ApiClient.java      - HTTP client for backend calls
panels/             - UI panel for each tool

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| /api/link-checker/scan | POST | Check a URL for threats |
| /api/file-scanner/scan | POST | Scan an uploaded file |
| /api/email-scanner/scan | POST | Check email for phishing |
| /api/password-checker/check | POST | Test password strength |
| /api/breach-checker/check | POST | Check email for breaches |
| /api/scan-history | GET | View all scan results |

## GitHub

https://github.com/Nusrath2345/Run
After saving it on GitHub, pull it locally so your branch stays in sync:
powershellgit checkout main
git pull origin main
git checkout feature/link-checker
git merge main
