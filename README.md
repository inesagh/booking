# Booking system

Before starting build and running the project please make sure that your current java version is java 21.0.8 2025-07-15 LTS.

1. Run docker compose to get PostgreSQL up and running
```shell
docker-compose up
```
2. Build the project
```shell
./gradlew clean build
```
3. Start the project
```shell
./gradlew bootRun 
```
4. After successful run open http://localhost:8080/swagger-ui/index.html in your browser
