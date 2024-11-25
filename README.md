# Database Migration Framework

This project is a custom database migration framework that
uses an XML changelog file to define and apply database
migrations. The framework is designed to be simple and 
flexible, allowing you to manage your database schema 
efficiently.

## Features
- **XML-Based Changelog**: Define migrations in a structured XML file.
- **Version system**: Run migrations to the current version.
- **Rollbacks**: Use of rollback scripts to get back to current version of database.


## Prerequisites
- **Java 21+**
- **PostgreSQL**
- **Maven**

## Installation and Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/Eexxlliinn/Task1

2. Configure the database connection in application.properties:
   ```bash
    db.url=jdbc:postgresql://localhost:5432/postgres
    db.username=postgres
    db.password=postgres

3. Build and run the application:
   ```bash
   mvn clean install
   
## Usage

1. Run script create-migration-history-table.sql

2. Store migrations and db.changelog-master.xml in db.changelog folder

3. Define migrations in db.changelog-master.xml
    ```bash
    <changelog>
        <changeSet id="1" version="1.0.0" file="your_file" rollback="your_rollback_file"/>
    </changelog>