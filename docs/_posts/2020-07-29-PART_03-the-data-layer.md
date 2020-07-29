---
layout: post
title: "PART 3: The data layer"
date: 2020-07-29 18:10:00 +0200
tags: unit_tests, component_tests, integration_tests
author: adriano
---

### On this page

1. 

## Repository Interface

Use PostgreSQL
Spring JpaRepository interface

add 
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

## Component Tests

create repositories package
@DataJpaTest is for
    Configure in-memory test database
    Auto scan @Entity classes

add the in-memory database
    <dependency><!-- Required for the data layer component test -->
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>

add the @Entity, @Id and @GeneratedValue to User

@Autowired
private TestEntityManager testEntityManager;
    to manage the in-memory database

@Autowired
private UsersRepository usersRepository;

add geddy and alex + constructor

@BeforeEach
@AfterEach

@Test find all users

public interface UserRepository extends JpaRepository<User, UUID>

First test should pass

## Adding More Tests

## Testing Against a Real Database

## Controlling Test Execution

You can find the full code for this part [here](https://github.com/nu75h311/lesson-scheduler/tree/part-03-the-data-layer).
