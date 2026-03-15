\# Setup Instructions



\## What you need installed

\- Java 17 (JDK)

\- Maven (added to PATH)

\- MySQL 8



\## Database setup

1\. Open MySQL Workbench

2\. Create a new database called run\_db by running: CREATE DATABASE run\_db;



\## Backend setup

1\. Open backend/run-backend/src/main/resources/application.properties

2\. Change the password to your own MySQL root password

3\. In a terminal run:

&nbsp;  cd backend/run-backend

&nbsp;  mvn clean spring-boot:run

4\. Go to http://localhost:8080/api/test to check it works



\## Frontend setup

1\. Make sure the backend is running first

2\. In a separate terminal:

&nbsp;  cd frontend

&nbsp;  javac RunApp.java

&nbsp;  java RunApp

3\. Click the button to test the connection



\## How to work on your module

1\. Pull latest from main

2\. Create a branch for your feature e.g. git checkout -b yourname/feature

3\. Do your work and commit as you go

4\. Push your branch and open a Pull Request when ready



\## Do not commit

\- Your MySQL password (change it locally, dont push it)

\- The target folder or .class files (already in .gitignore)

