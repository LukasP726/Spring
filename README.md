# Spring Boot Project README

## Requirements

To run this project, you need the following:

- **Java Development Kit (JDK)**
    - Minimum required version: **Java 17**.
    - Download and install from: [https://adoptium.net/](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html).

- **Maven**
    - The project uses **Maven Wrapper**, so you don't need to have Maven installed on your system.
    - Maven Wrapper will automatically download the correct version of Maven when you run it.

- **Database**
   - The project uses **MySQL** as the database management system. Ensure that MySQL is installed and properly configured on your machine.  
   - The required version of MySQL is **8.0 or higher**.
   - The database schema is provided in the file located at `src/main/resources/forum.sql`. Import this file into your MySQL database to set up the required tables and data.  
   - Configuration details for connecting to the database can be found in `src/main/resources/application.properties`.

## Project Setup

1. Clone or download this repository to your local machine.

2. Navigate to the project directory:
    ```bash
    cd /path/to/your/project
    ```

3. Configure the database connection in the `src/main/resources/application.properties` file:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/your_database
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```

## Running the Project

### Windows

To start the application, run:
    ```bash
    ./mvnw.cmd spring-boot:run
    ```

### Linux/MacOS

Use this command instead:
    ```bash
    ./mvnw spring-boot:run
    ```

Once the application starts, it will be available at:
    ```
    http://localhost:8080
    ```

## Building the Project

To create a JAR file for deployment, use the following command:
    ```bash
    ./mvnw package
    ```

The JAR file will be generated in the `target` directory. You can run it with:
    ```bash
    java -jar target/your-application.jar
    ```
