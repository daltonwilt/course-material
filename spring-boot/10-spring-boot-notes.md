# Spring | Basics

## Table of Contents

**Fundamentals**

1. [What Is the Spring Framework](#what-is-the-spring-framework)
2. [What Is Spring Boot](#what-is-spring-boot)
3. [Core Features of the Spring Framework](#core-features-of-the-spring-framework)

**Beans & Dependency Injection**

4. [What Is a Spring Bean](#what-is-a-spring-bean)
5. [Bean Lifecycle](#bean-lifecycle)
6. [Dependency Injection](#dependency-injection)
7. [Bean Scoping](#bean-scoping)
8. [Environment & Profiles](#environment--profiles)

**Reference**

9. [Annotation Reference](#annotation-reference)

**REST APIs**

10. [REST Overview](#rest-overview)
11. [Resource Design](#resource-design)
12. [Data Validation](#data-validation)

**Data & Architecture**

13. [POJOs vs. Java Records](#pojos-vs-java-records)
14. [Data Transfer Objects (DTO)](#data-transfer-objects-dto)
15. [Service Layer](#service-layer)
16. [Code Organization Approaches](#code-organization-approaches)

**Getting Started**

17. [Best Practices](#best-practices)
18. [Spring Initializr](#spring-initializr)

## What Is the Spring Framework

**Open source.** The Spring Framework is an open-source framework for building enterprise Java applications.

**Purpose.** It aims to simplify complex enterprise Java application development, achieving this by offering technologies such as Aspect-Oriented Programming, Dependency Injection, plain old Java objects, and more.

**Lightweight.** It is a lightweight framework that can be used to create scalable, secure, and robust enterprise applications. At a micro level, Spring can be considered a collection of sub-frameworks such as Spring Web, Spring MVC, Spring Security, Spring Data, and more.

## What Is Spring Boot

**Spring-based.** Spring Boot is an approach to developing Spring-based applications with very little or no configuration.

**Starters.** Spring Boot provides a set of starters (POM/Gradle build files) that can be used to add required dependencies and facilitate auto-configuration.

**Auto-configuration.** Spring Boot ships with many auto-configurations, so depending on the libraries on its classpath, Spring Boot can automatically configure the required classes.

### Why Spring Boot

- Standalone applications
- Embedded server
- Starters
- Auto-configuration
- Production-ready features
- No XML configuration

## Core Features of the Spring Framework

### Inversion of Control Container (IoC)

- One of the core features of Spring, providing a streamlined way to configure and manage Java objects. This container is responsible for managing the lifecycle of a defined Java object, significantly increasing the configurability of a Spring-based application.
- IoC uses dependency injection or dependency lookup patterns to provide object references during runtime.

### Aspect-Oriented Programming (AOP)

- Aims to provide more modularity to cross-cutting concerns — functions that span across the application, such as logging, caching, transaction management, authentication, and more.

### Data Access Framework (DAF)

- Spring simplifies database communication by providing direct support for popular data access frameworks such as JDBC, Hibernate, and the Java Persistence API (JPA).
- Additionally, it offers Resource Management, Exception Handling, and Resource Wrapping for all supported data access frameworks, further simplifying development.

### Spring MVC Framework

- Enables developers to create applications using the popular MVC pattern. It is a request-based framework that allows developers to easily create customized MVC implementations that suit their needs.
- The core component is the **dispatcher servlet**, which handles user requests and forwards them to the correct controller. This allows the controller to process the request, create the model, and provide the information to the end user via a specified view.

## What Is a Spring Bean

**Object.** An object that is managed by the Spring Framework in a Java application.

**What Spring does for it.**

- The Spring Framework creates beans, manages their lifecycle, and organizes their dependencies with other beans.
- It takes care of the instantiation, configuration, and wiring of objects — saving developers a lot of manual work.

**Configuration.** Spring beans can be configured using XML, Java annotations, or Java code.

## Bean Lifecycle

**Object lifecycle.** The lifecycle of any object means when and how it is born, how it behaves throughout its life, and when and how it dies. Similarly, the bean lifecycle refers to when and how the bean is instantiated, what actions it performs during its life, and when and how it is destroyed.

**Managed by the Spring container.** When the program is run, the container starts, creates the instance of a bean per the request, and injects dependencies. Finally, the bean is destroyed when the Spring container is destroyed.

---

## Dependency Injection

| Type                       | How it works                                                                                                                                                                   |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Constructor**            | Constructor parameters receive dependencies during bean construction.                                                                                                          |
| **Field**                  | Field declarations receive dependencies injected via reflection access, without constructor or method delegation.                                                              |
| **Configuration (method)** | One or many parameters receive dependencies through method parameters. Allows setting several dependencies in one method, and allows initialization work while receiving them. |
| **Setter (method)**        | A Java setter method takes one parameter and follows the JavaBean naming convention. Has two variants: type injection or name injection.                                       |

### Constructor-Based vs. Setter-Based

- Use **constructors for mandatory dependencies** and **setters or configuration methods for optional dependencies**.
- The Spring team generally advocates constructor injection.

> ⚠️ **Field injection is discouraged.** It makes testing components in isolation more complex, so it should only be used in test classes.

## Bean Scoping

**What is a bean scope?** It refers to the lifecycle of a Spring bean and its availability in the context of the application. Spring provides multiple scopes.

**Singleton** is the default scope — one instance per application context. The shared instance will be accessed by other components, therefore components must be thread-safe.

### Scope Types

Set with `@Scope` / `@SessionScope`.

| Scope                   | Description                                                                                                                                                                                       |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Singleton** (default) | One instance of the bean is created, and all requests for that bean receive the same instance. Useful for beans that don't hold state, or where the same state is shared by all users or threads. |
| **Prototype**           | A new instance is created upon each request from the container. Useful for beans that carry state specific to a user or thread and therefore can't be shared.                                     |
| **Request**             | Web-aware contexts only. A new bean is created for each HTTP request.                                                                                                                             |
| **Session**             | Web-aware contexts only. A new bean is created for each HTTP session by the container.                                                                                                            |
| **Application**         | Web-aware contexts only. Scoped to the lifecycle of a servlet context — i.e. the application level.                                                                                               |
| **WebSocket**           | Web-aware contexts only. Scoped to the lifecycle of a WebSocket — i.e. the socket level.                                                                                                          |

## Environment & Profiles

### Environment

- An environment abstraction (Local / Dev / Cloud / Prod / …) that decouples application code from the environment, with support for bean definition profiles that allow different sets of beans depending on the environment.
- Injectable.

### Profiles (`@Profile`)

- A named logical grouping that may be activated programmatically or set active through configuration.
- Enables different configuration depending on the environment.

### Profile Activation

**Programmatically:**

```java
AnnotationConfigApplicationContext applicationContext = ...;
applicationContext.getEnvironment().setActiveProfiles("cloud");
applicationContext.getBean(MyBean.class);
```

**Via properties file:**

```yaml
# application.yaml
spring:
  profiles:
    active: cloud
```

```properties
# application.properties
spring.profiles.active=cloud
```

---

## Annotation Reference (Spring Basic)

| Annotation                               | Purpose                                                                                          | Notes                                                                                                                                                                                                                                    |
| ---------------------------------------- | ------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `@Configuration`                         | Declares a class as a "full" configuration class. Spring scans configuration classes at startup. | Class must be non-final and public.                                                                                                                                                                                                      |
| `@Bean`                                  | Declares a bean configuration inside a configuration class.                                      | Method must be non-final and non-private (public, protected, or package-private).                                                                                                                                                        |
| `@Component`                             | Marks a class as a Spring component.                                                             | See [below](#component-and-stereotypes).                                                                                                                                                                                                 |
| `@Repository`, `@Service`, `@Controller` | Meta-annotations for `@Component` that further refine components.                                |                                                                                                                                                                                                                                          |
| `@Qualifier`, `@Primary`                 | Disambiguate which bean to inject.                                                               | `@Qualifier("primary")` is equivalent to `@Primary` as a shorthand.                                                                                                                                                                      |
| `@Scope`, `@SessionScope`                | Set the bean scope.                                                                              | e.g. `@Scope("prototype")` — any of the six [scopes](#scope-types). Default is singleton.                                                                                                                                                |
| `@Profile`                               | Restricts a bean to a given profile.                                                             | e.g. `@Profile("cloud")` makes the bean available only in the cloud profile.                                                                                                                                                             |
| `@Value`, `@PropertySource`              | Expression-driven dependency injection at field, method, or constructor-parameter level.         | e.g. `@Value("${jdbc.url}")`, `@PropertySource("classpath:database.properties")`. Also supports dynamic expressions to access other beans or well-known beans such as system properties: `@Value("#{systemProperties['user.region']}")`. |
| `@Import`                                | Imports split configuration into another class.                                                  | e.g. `@Import({ServiceConfig.class, RepositoryConfig.class})`.                                                                                                                                                                           |
| `@Autowired`                             | Marks a constructor, field, or setter for injection.                                             | Optional on a constructor if the class has **only one** constructor.                                                                                                                                                                     |

### `@Component` and Stereotypes

- A class-level annotation that marks a class as a Spring component, indicating it should be initialized and managed by the core container.
- Constructor dependency injection is done automatically via `@Autowired` on the constructor parameter(s).
- Spring provides component **stereotypes** to classify classes as Spring components; sub-types are available as refinements of the standard component.
- Your own stereotype annotations can (and should) be defined to support general architecture principles.

## REST Overview

**REST** — Representational State Transfer.

### Architectural Constraints

| Constraint            | Description                                                                                                                  |
| --------------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| **Client–Server**     | The client and server act independently. They interact with each other, but each side can be developed independently.        |
| **Stateless**         | Each request contains all information necessary to understand and respond to it. The server stores no data between requests. |
| **Cacheable**         | Responses must explicitly define themselves as cacheable.                                                                    |
| **Layered System**    | The architecture is composed of hierarchical layers.                                                                         |
| **Code on Demand**    | _(Optional)_ The server can extend client functionality by transferring executable code.                                     |
| **Uniform Interface** | See below.                                                                                                                   |

**Uniform interface** comprises:

- Identification of resources
- Manipulation of resources through representations
- Self-descriptive messages
- Hypermedia as the Engine of Application State (HATEOAS)

### The Web's Architectural Principles

- Unique identification of resources (URI)
- Different resource representations
- Hypermedia / linking of resources
- Stateless communication
- Standard methods (GET, POST, PUT, DELETE) and responses (200 OK, 404 Not Found)

## Resource Design

Resources should always be **plural nouns** in the API endpoint. To retrieve a single instance, pass the id in the URL:

```http
GET    /accounts
GET    /accounts/{id}
DELETE /accounts/{id}
```

For nested resources:

```http
GET /accounts/{id}/payments/{id}
```

### HTTP Methods

Use HTTP methods to specify what to do with the resource. With GET, POST, PUT, PATCH, and DELETE you can provide full CRUD functionality (Create, Read, Update, Delete).

| Method   | Action              |
| -------- | ------------------- |
| `GET`    | Get / retrieve data |
| `POST`   | Create data         |
| `PUT`    | Update data         |
| `DELETE` | Delete data         |

## Data Validation

Primary functions of data validation in a REST API:

- **Data integrity**
- **Preventing attacks**
- **Error prevention**
- **User experience (UX)**
- **Performance**
- **Business logic compliance**

## POJOs vs. Java Records

### Java Record

- A shallowly immutable, transparent carrier for a fixed set of values.
- Values can be accessed with accessor methods that have the same names as the fields.
- Records reduce the verbosity of Java and make it more suitable for data-centric applications.
- Records are final by default, and all their fields are final — so they can't be used with libraries that require mutable beans, such as older versions of Hibernate or JPA.

### POJO

- Versatile and works with all Java versions, but requires more code to write.
- You must declare fields and generate the constructor, getters, setters, `equals`, `hashCode`, and `toString`.

> **Guidance:** If you are using Java 16 or later and your DTOs are simple data carriers without additional logic, records may be the better choice. They provide concise, convenient syntax and automatic implementations of `equals`, `hashCode`, and `toString`.

## Data Transfer Objects (DTO)

A pattern used in software design. The main purpose of a DTO is to encapsulate and structure data that needs to be transferred between different parts of a system, or between different systems entirely. A DTO typically includes only simple data fields (attributes) and lacks the behavior of the model or entity it represents.

```
                     ┌ Representation 1 ┐          ┌ Student (Not PII) ┐
Client <-> REST API <┤ Representation 2 ├> Mapper <┤                   ├> Database
                     └ Representation 3 ┘          └ School            ┘
```

### Why DTOs Matter

- **Data separation** — A DTO helps separate the internal domain model from what is exposed through the API. This way you can change your internal model without affecting the external representation. It also helps avoid exposing sensitive or unnecessary data.
- **Abstraction** — An abstraction layer that gives a clear structure of what your API provides to the client, abstracting the complexity of the domain model.
- **Performance** — Instead of sending entire entities over the network, you send DTOs that include only the necessary information. This improves API performance since you control the size of your response payload.
- **Flexibility** — Allows you to tailor your API response to what your client needs, even if that doesn't exactly match your domain model.
- **Versioning** — Makes it easier to maintain different versions of your API; by using different sets of DTOs you can support multiple API versions simultaneously.

## Service Layer

```
Controller <-> Service <-> Data Layer
```

The service layer separates the concerns of handling business logic and application-specific operations from the presentation layer (controller) and the data access layer (repository). It serves as an intermediary between the controller and the repository, encapsulating business logic and providing a reusable, modular, and testable code base.

- **Business logic encapsulation** — The service layer implements the business logic and performs complex operations required by the API. It encapsulates logic that goes beyond simple data retrieval or modification, such as validation, calculation, transformation, or coordination of multiple operations (orchestration).
- **Separation of concerns** — Controllers handle incoming HTTP requests and provide appropriate responses, while repositories handle data persistence. The service layer sits in between, ensuring business logic and application-specific operations are decoupled from the other layers.
- **Testing and maintainability** — Provides a boundary for unit testing, as you can test business logic independently of controllers and repositories. By mocking or stubbing the dependencies, you can focus solely on testing the logic within the service methods.

## Code Organization Approaches

### By Feature (Preferred)

Code is organized around the business capabilities or features of the application. Each package contains all classes related to that feature. This is beneficial for large teams and projects because it is easy to locate code related to a specific feature.

```
com
└── app
    ├── product
    ├── order
    ├── customer
    └── payment
```

### By Layer

Code is organized around its architectural layers. This is beneficial when the focus is more on the technical roles of classes rather than their business roles. The downside is that a change to a single feature could affect multiple packages.

```
com
└── app
    ├── controllers
    ├── services
    ├── repositories
    ├── models
    └── utils
```

### Domain-Driven Design (DDD)

The focus is on business domain packages, so packages are formed around the different bounded contexts or subdomains within the larger domain. For instance, in a health care system:

```
com
└── app
    ├── patientManagement
    ├── billing
    ├── scheduling
    └── medicalRecords
```

### By Component

A modularized approach with top-level packages for each high-level component of your system. Within each of these packages you can further organize code by feature or layer.

```
com
└── app
    ├── usercomponent
    │   ├── controllers
    │   └── services
    └── productcomponent
        ├── controllers
        └── services
```

## Spring Testing

### Importance of Testing

- Quality Assurance
- Regression Testing
- Documentation
- Code Maintainability
- Refactoring Confidence
- Collaboration
- Continuous Integration/Continuous Deployment (CI/CD)
- Reduced Debugging Time
- Scalability
- Security

## Spring Test - Introduction

- Spring Boot provides utilities and annotations for testing applications
- Test support is provided by two modules
  - spring-boot-test contains core items
  - spring-boot-test-autoconfigure supports auto-configuration for tests
  - spring-boot-starter-test "Starter", imports:
    - Spring Boot test modules
    - JUnit, AssetJ, Hamcrest, and a number of other useful libraries
- Spring Boot application is a Spring ApplicationContext
- Spring Boot provides a `@SpringBootTest` annotation when you need Spring Boot features during test
- JUnit 4 -> `@Runwith(SpringRunner.clas)` to your test
- JUnit 4 -> No need to add the equivalent `@ExtendWith(SpringExtension)`
- Spring Boot's auto-configuration system works well for applications but can sometimes be a little too much for tests
- It often helps to load only the parts of the configuration that are required to test a "slice" of your applicaiton
- The spring-boot-test-autoconfiguration module includes a number of annotations that can be used to automatically configure such "slices"
- Each slice restricts component scan to appropriate components and loads a very restricted set of auto-configuraiton class

## Hibernate VS Spring Data JPA

```
Spring Data JPA <-> JPA <-> Hibernate (JDBC) <-> DB Volume
```

- Spring Data JPA is an abstraction layer on top of JPA to reduce the amount of boilerplate code required to implement data access object (DAO)
- JPA is just a specification that facilitates Object Relational Mapping ot manage relational data in Java applications
- Hibernate is a JPA implementation
- Hibernate generates SQL query and executes using JDBC

### Repository Abstraction
- The Repositories abstraction is to reduce the amount of boilerplate code needed to implement the DAO (Data Access Object) to interact with the data stores
- Spring Data JPA a repository is a Java Interface that is used to abstract the data access layer from the business logic
- Used to perform CRUD operations (Create/Read/Update/Delete) including other operations such as Paging/Sorting

## Annotation Reference (JPA)

| Annotation   | Purpose             | Notes               |
| ------------ | ------------------- | ------------------- |
| `@Id` | Identifies the Primary Key of an Entity | |
| `@GeneratedValue` | Specifies a Generation Strategy for generated primary keys (Auto) chooses the strategy determined to be best suited | TABLE/SEQUENCE/IDENTITY/UUID |


## Best Practices

### Split Configuration Classes

Configuration can be split into several classes — first to avoid large configuration classes, and second to allow splitting based on architecture or other drivers. Classes can then import each other, or the application context can be constructed with multiple classes.

### Import Split Configuration (`@Import`)

Import split configuration classes as a best practice to avoid large configurations.

## Spring Initializr

Provides a simple web UI to configure a project, and generates an endpoint you can also use via plain HTTP.

| Option                  | Choices                                 |
| ----------------------- | --------------------------------------- |
| **Project**             | Gradle – Groovy, Gradle – Kotlin, Maven |
| **Language**            | Java, Kotlin, Groovy                    |
| **Spring Boot Version** | —                                       |
| **Dependencies**        | —                                       |

**Project metadata:**

- Group
- Artifact
- Name
- Description
- Package name
- Packaging (Jar / War) — _Jar is recommended with Spring Boot_
- Java version

---
[← 10 · Spring Boot](10-spring-boot.md) · [11 · REST API Design](11-rest-api-design.md)
