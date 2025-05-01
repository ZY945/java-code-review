Create a Spring Boot project scaffold named 'SpringScaffoldBase' that serves as a robust foundation for future projects. The project should include the following components and configurations, ensuring best practices and a production-ready setup, with detailed example implementations for MyBatis-Plus and RabbitMQ:

1. **Core Dependencies**:
   - Use Spring Boot 3.x (latest stable version).
   - Include dependencies for:
     - Spring Web (for RESTful APIs).
     - RabbitMQ (for message queue integration with Spring AMQP).
     - Redis (for caching and session management using Spring Data Redis).
     - Redisson (for distributed locks and advanced Redis features).
     - MyBatis-Plus (for ORM and database operations).
     - Spring Boot Starter Validation (for input validation).
     - Lombok (to reduce boilerplate code).
     - Spring Boot Starter Actuator (for monitoring and health checks).
     - Springdoc OpenAPI (for Swagger UI documentation).

2. **Project Structure**:
   - Organize the project using a standard layered architecture: controller, service, repository, entity, dto, config, exception, utils.
   - Provide a sample entity, DTO, repository, service, and controller to demonstrate usage, with specific examples for MyBatis-Plus and RabbitMQ as detailed below.

3. **Result Handling**:
   - Implement a unified response wrapper class `Result<T>` for all API responses, supporting success and error cases.
   - Include methods for common response patterns (e.g., `success(T data)`, `failure(int code, String message)`).

4. **Global Exception Handling**:
   - Create a global exception handler using `@ControllerAdvice` to catch and handle exceptions (e.g., validation errors, business exceptions, and unexpected errors).
   - Define custom exception classes (e.g., `BusinessException`) for specific error scenarios.
   - Return errors in the `Result<T>` format with appropriate HTTP status codes.

5. **Web Configuration**:
   - Configure a `WebConfig` class implementing `WebMvcConfigurer` to:
     - Enable CORS support for all origins.
     - Add a logging interceptor to log request details (e.g., URL, method, execution time).
     - Configure Jackson for JSON serialization/deserialization (e.g., ignore null fields, use ISO date format).
   - Set up Swagger UI with Springdoc OpenAPI, including API metadata (title: "SpringScaffoldBase API", version: "1.0.0").

6. **Database Configuration (MyBatis-Plus)**:
   - Configure MyBatis-Plus with:
     - MySQL as the database (include a sample `application.yml` with MySQL connection properties).
     - Pagination support using `MybatisPlusInterceptor`.
     - Optimistic locking with `@Version` annotation.
     - Auto-fill fields (e.g., `create_time`, `update_time`) using `MetaObjectHandler`.
   - Provide a sample entity with annotations (e.g., `@TableName`, `@TableId`, `@TableField`, `@Version`).
   - **MyBatis-Plus Example Implementation**:
     - Create an entity class `User` with fields: `id` (auto-increment), `username`, `email`, `createTime`, `updateTime`, `version`.
     - Create a `UserMapper` interface extending `BaseMapper<User>`.
     - Create a `UserService` with methods for CRUD operations (e.g., save, update, delete, query by ID, paginated query).
     - Create a `UserController` with REST endpoints to demonstrate CRUD operations (e.g., POST `/users`, GET `/users/{id}`, GET `/users/page`).
     - Include a sample SQL script to create the `user` table in MySQL.

7. **RabbitMQ Configuration**:
   - Set up RabbitMQ with Spring AMQP.
   - Configure a direct exchange (`order.exchange`), a queue (`order.queue`), and a binding with routing key (`order.routing`).
   - Enable publisher confirms and returns for reliable message delivery.
   - Include error handling for message processing (e.g., retry mechanism, dead-letter queue).
   - **RabbitMQ Example Implementation**:
     - Create a `OrderMessage` DTO with fields: `orderId`, `userId`, `amount`, `createTime`.
     - Create a `MessageProducer` class with a method to send `OrderMessage` to the `order.queue`.
     - Create a `MessageConsumer` class to consume messages from `order.queue`, logging the message details.
     - Create a `OrderController` with a POST endpoint (e.g., `/orders/send`) to trigger sending an `OrderMessage`.
     - Provide a configuration class `RabbitMQConfig` to define the exchange, queue, and binding programmatically.

8. **Redis Configuration**:
   - Configure Spring Data Redis with Lettuce as the client.
   - Provide a `RedisTemplate` configuration for string and object serialization.
   - Include a `RedisUtil` class with methods for common operations (e.g., `set`, `get`, `delete`, `expire`).

9. **Redisson Configuration**:
   - Configure Redisson for distributed locks (single Redis server mode).
   - Provide an example in `UserService` using Redisson's `RLock` to ensure thread-safe user creation.

10. **Configuration Files**:
    - Create an `application.yml` with profiles (dev, prod) for:
      - MySQL connection (e.g., `spring.datasource.url`, `username`, `password`).
      - RabbitMQ connection (e.g., `spring.rabbitmq.host`, `port`, `username`, `password`).
      - Redis connection (e.g., `spring.redis.host`, `port`, `password`).
      - Redisson configuration (single server mode with YAML config).
      - Logging levels (e.g., `INFO` for root, `DEBUG` for project packages).
      - Actuator endpoints exposure (e.g., `/actuator/health`, `/actuator/info`).
    - Include a `logback-spring.xml` for structured logging with console and file appenders.

11. **Additional Features**:
    - Add a basic Spring Security setup with JWT authentication (configurable via properties, disabled by default in dev profile).
    - Include a `CommonUtil` class for utilities (e.g., UUID generation, date formatting).
    - Configure a thread pool for asynchronous tasks using `@EnableAsync` and `ThreadPoolTaskExecutor`.
    - Provide a sample `Dockerfile` for containerization (based on OpenJDK).
    - Include a `README.md` with:
      - Project setup instructions (e.g., install MySQL, RabbitMQ, Redis).
      - Instructions to run the application (`mvn spring-boot:run`).
      - Example API calls using Swagger UI.
      - Instructions to test RabbitMQ producer/consumer.
      - Instructions to test MyBatis-Plus CRUD operations.

12. **Best Practices**:
    - Follow Spring Boot best practices for dependency injection, configuration, and exception handling.
    - Use SLF4J with Logback for logging, with clear log messages for debugging.
    - Ensure thread safety for Redis and Redisson operations.
    - Write clean, maintainable, and well-documented code with JavaDoc for key classes and methods.

13. **Artifacts**:
    - Generate the full project structure with all necessary files (e.g., `pom.xml`, Java classes, configuration files, `Dockerfile`, `README.md`, SQL script).
    - Wrap each file in an `<xaiArtifact>` tag with a unique UUID, appropriate title, and content type.
    - For `pom.xml`, include all required dependencies and plugins (e.g., Spring Boot Maven Plugin, MyBatis-Plus Generator).
    - For Java classes, use package naming `com.example.springscaffoldbase`.
    - Include the SQL script for the `user` table in a file named `init.sql`.

Please provide the complete project scaffold, ensuring all components are integrated and functional. For MyBatis-Plus, include a fully working CRUD example with `User` entity, mapper, service, and controller. For RabbitMQ, include a fully working producer/consumer example with `OrderMessage`. Assume reasonable defaults for configurations (e.g., direct exchange for RabbitMQ, single Redis server). If any clarification is needed, prioritize simplicity and usability.