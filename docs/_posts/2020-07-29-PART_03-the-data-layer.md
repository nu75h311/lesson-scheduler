---
layout: post
title: "PART 3: The data layer"
date: 2020-07-29 18:10:00 +0200
tags: unit_tests, component_tests, integration_tests
author: adriano
---

### On this page

1. Define what will be used for [communication](#repository-interface) with the DB
2. Create data layer [component tests](#component-tests)
3. See how easy it is to [create more ways](#adding-more-functionality) to interact with the DB

## Repository Interface

In the last part we created our model (the User object) and some tests to basically check Jackson parsing compatibility. Now we are going to touch the data layer, the one that communicates with the database. For our database choice, it's going to be [PostgreSQL](https://www.postgresql.org/), an open source relational database.  
Because we are using Spring, we can leverage the power of its [Java Persistence API support](https://docs.spring.io/spring-data/jpa/docs/current/reference/html), which requires only an interface to make our application able to communicate with the database.

To make use of the Spring `JpaRepository` interface, add the following dependency to the `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

## Component Tests

We will create tests to check if the repository that Spring instantiates out of `JpaRepository` has all of its DB communication methods working, but because at this point we will use a real (in-memory dummy, but still real) database, I would call them component tests, for we are now outside of the unit level realm (opinions may vary!).

Let's start by creating a `com.lessonscheduler.user.repositories` package, both for `main` and `test`. Then under the test folder, create a `UserRepositoryTests` class. Because of the `spring-boot-starter-data-jpa` dependency, we can declare this class a `@DataJpaTest` by adding this annotation to it. This will tell Spring to 1) configure an in-memory database to run the tests against, and 2) scan the application code for `@Entity` classes, so that it knows what type of data (tables) it will be dealing with.

```java
@DataJpaTest
public class UserRepositoryTests {
}
```

To be able to use an in-memory database, we need to have one available. Add the following dependency to the `pom/xml`:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

And then we need to add the following annotations to the `User.java` class:

```java
...
@Entity
public class User {

    @Id
    @GeneratedValue
    private UUID id;
    ...
}
```

When we declare an `@Entity`, we need to add the `@Id` to one of the attributes and, because we will be building User object instances without passing the `id` attribute, we should also declare that the `id` is a `@GeneratedValue`.  
Now the User class should be ready to be treated as data.

For Spring to auto-configure and for us to manage the in-memory database, we need to declare and `@Autowire` a `TestEntityManager` in the test class. Let's also declare and `@Autowire` the repository that we are yet to create in the `UserRepositoryTests.java`:

```java
@Autowired
private TestEntityManager testEntityManager;
@Autowired
private UserRepository userRepository;
```

Now to make the tests more focused, we could initialize our database with some data from the get go. A way to do that is to initialize some Users in the test class constructor and persist them to the database `@BeforeEach` test:

```java
private final User geddy;
private final User alex;

public UserRepositoryTests() {
    geddy = User.builder().firstName("Geddy")
                          .lastName("Lee")
                          .email("geddy.lee@email.com")
                          .build();
    alex = User.builder().firstName("Alex")
                         .lastName("Lifeson")
                         .email("alex.lifeson@email.com")
                         .build();
}

@BeforeEach
void setup() {
    this.testEntityManager.persist(geddy);
    this.testEntityManager.persist(alex);
}
```

Also, let's not forget to clean up `@AfterEach` test:

```java
@AfterEach
void cleanup() {
    this.testEntityManager.clear();
}
```

Ok, now we can write our first test. Because the database is initialized with 2 users, let just confirm that:

```java
@Test
@DisplayName("Find all users")
public void testFindAllUsers() {
    List<User> users = userRepository.findAll();

    assertAll(
            () -> assertThat(users.contains(geddy)),
            () -> assertThat(users.contains(alex)),
            () -> assertThat(users.size()).isEqualTo(2));
}
```

And finally, to make this test pass, all we need to do is create a repository interface that extends Spring's `JpaRepository`, telling it which java object to base the table on and what is the entity's id:

```java
public interface UserRepository extends JpaRepository<User, UUID> {    
}
```

With only that, the first test should pass.

## Adding More Functionality

All other basic interactions with the repository (`findById()`, `saveAndFlush()`, `deleteById()`, etc.) should also be available now. We can have tests for all of these (you can find them in the repo), but the interesting feature of Spring's `JpaRepository` interface is that you can specify custom methods in the `UsersRepository` interface and they will be implemented automatically, given that you follow a simple pattern for defining the methods' names.  
For example, to have a method in your interface that will find users in the database by email like this:

```java
@Test
@DisplayName("Find user by email")
public void testFindUserByEmail() {
    User user = userRepository.findUserByEmail("alex.lifeson@email.com");

    assertAll(
            () -> assertThat(user.getId()).isNotNull(),
            () -> assertThat(user.getFirstName()).isEqualTo("Alex"),
            () -> assertThat(user.getLastName()).isEqualTo("Lifeson"),
            () -> assertThat(user.getEmail()).isEqualTo("alex.lifeson@email.com"));
}
```

...you can just declare the following method in the interface class:

```java
public interface UsersRepository extends JpaRepository<User, UUID> {

    User findUserByEmail(String email);
}
```

## Testing Against a Real Database

## Controlling Test Execution

You can find the full code for this part [here](https://github.com/nu75h311/lesson-scheduler/tree/part-03-the-data-layer).
