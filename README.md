# E-Mobility Charging Solutions Platform

This project is a Spring Boot-based REST API for managing Trip Detail Records (CDR) in real-time for a network of 
Trip Point Operators (CPO).
The system uses range-based sharding to efficiently manage large datasets by distributing records across multiple
PostgreSQL databases based on the `startTime` of the charging session.
Each Year has its DB instance where old data can be removed easily.
The configuration of the shards in the classes can be configured.
## Features

- **REST API**: Provides endpoints to create, retrieve, and search Trip Detail Records.
- **Secured endpoint**: The endpoints are secured by AUTH_TOKEN where it is sent by the header (X-API-KEY: AMIR).
- **Range-Based Sharding**: Data is sharded across multiple PostgreSQL instances based on date ranges by year.
- **Validation**: Ensures data integrity, such as `endTime` being greater than `startTime`, and `totalCost` being positive.
- **Integration with PostgreSQL**: Uses PostgreSQL for data storage, with sharding to support scalability.
- **Dockerized Environment**: Docker Compose setup for running the application with multiple PostgreSQL instances.
- **Caching service level**: Using service cache 

## Technologies Used

- **Java 21**
- **Spring Boot**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL**
- **H2**
- **Docker & Docker Compose**
- **Maven** (Build Tool)
- **JUnit** (Testing)
- **Swagger** (API Documentation)
- **flyway**
- **Virtual Threads**

## Prerequisites

- **Java 21**
- **Docker & Docker Compose**
- **Maven** (or use .\mvnw)
 
## Project Structure

- `src/main/java/com/emobility/cdr/`: Java source files.
- `src/main/resources/`: Configuration files, including `application.yml`.
- `src/test/java/com/emobility/cdr/`: Test cases.
- `docker-compose.yml`: Docker Compose file to set up multiple PostgreSQL instances and the Spring Boot application.
- `db/migration`: SQL files to initialize data for each year shard.

## Setup and Running


### 1. Build and Run with Docker Compose

```bash
mvn clean install
docker-compose up --build
```

These command will:

- Build the Spring Boot application including integration tests with H2 in memory.
- Start two PostgreSQL instances as shards.
- Start the Spring Boot application connected to these shards.
- Go to http://localhost:8080/swagger-ui/index.html to see its endpoint properties (for security reason, the endpoints will not be executed)
- Also, you can use intellij built in HTTP Client or Postman for running the api including the header "X-API-KEY: AMIR":

### 2. Accessing the API

Once the application is running, you can access the REST API at:

- **API Base URL**: `http://localhost:8080/crd/`

### 3. Database Configuration

The application is configured with two PostgreSQL databases:

- **Shard 1**: `cdr_db_shard_1` (For records with `startTime` before 2024)
- **Shard 2**: `cdr_db_shard_2` (For records with `startTime` from 2024 onwards)

The sharding logic routes queries to the appropriate database based on the `startTime` field.

### 4. Initializing Data

To populate the databases with initial data, Flyway is being implemented for that and it has the data and schema under 
resources/db/migration
different data can be migrated including schema



### 5. Testing

The project includes integration tests that can be run using Maven:

```bash
mvn test
```
It runs two H2 databases that resemble postgresql as shards 

### 6. Swagger Documentation

Swagger is available at:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

Only the endpoints can be seen but not used due to security

### 7. Sharding Logic

The sharding logic is implemented in the `TdrShardingService` class, which routes database operations based on the `startTime` of the CDR:

- **Shard 2024**: Handles data with `startTime` for 2024 records.
- **Shard 2023**: Handles data with `startTime` from 2023 records.

for new years the configuration can be added in the class

## Known Issues

- **Unknown Business**: Due to unknown business knowledge, several assumptions we present to develop the solution.
- **unique Session id**: Make session id unique across databases validation.require another check across shards.
- **Limited Shards**: Currently, the system is set up with only two shards. Additional shards could be added based on future needs.
- **Trace Logs**: Currently, the system has no trace logs for the requests since it requires more technologies(micrometer,apm..etc) and time for implementations.
- **Overlapping data**: Optimize db calls when having overlapping data in different shards from multiple databases.
- **non-existing vehicle id**: Manage db strategy for calling different dbs with non-existing vehicle id.
- **Requests Validations**: Make validation for the requests with different cases.

## Future Improvements

- **Automated Schema Management**: Use Flyway or Liquibase for managing database schemas across shards.
- **Enhanced Sharding Strategy**: Consider dynamic sharding strategies or shard rebalancing based on load.
- **Monitoring and Alerting**: Implement monitoring for database health and performance.
- **Increase testing data for pagination**: Add more testings for different cases of pagination's and edge cases.
- **Create more profiles**: Add profiles for dev, production and staging environments, also update docker compose accordingly.
- **Pages Info**: Handle return results with page number info.
- **Redis cache**: Use Redis Cache for retrieving Record according to sessionId.
- **Swagger with testing**: Enable swagger for testing env only.
- **Thread**: Optimize using threads in the application.



