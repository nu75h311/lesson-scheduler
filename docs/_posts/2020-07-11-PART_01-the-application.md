---
layout: post
title: "PART 1: The application"
date: 2020-07-11 16:21:59 +0200
tags: business bdd system_tests
author: adriano
---

## On this page

1. Define some business scenarios, [BDD](#bdd) style
2. Write [high level tests](#system-tests)

## BDD

In [Behavior Driven Development](https://cucumber.io/docs/bdd/) fashion (and I am not talking about gherkin!), let's start by trying to describe this application via an elevator pitch:

> "It's an app where every user can create lessons - with defined start and end date/time - for which (s)he will be the teacher, and every other user can sign up for that lesson as a student."

Ok, that could have come from a business analyst describing the app from 10, 000 feet. From this altitude, let's imagine that there were some discussions and some very high level Epics came to existence:

* Anyone can register with an email
* Any user can create lessons
* Any user can sign up for other users' lessons
* Users should not be able to give/attend more than one lesson at the same time

> Note that we will only deal with the scheduling requirements. How these lessons are given is irrelevant to this exercise.

Now the team can have some [Example Mapping](https://cucumber.io/blog/bdd/example-mapping-introduction/) sessions to come up with the scenarios that specify these epics.

For the first epic, we came up with these scenarios:

> **Feature**: User registration
>
>> **Rule**: Email in User registration must be unique
>>> **Scenario**: the one where a person tries to register with an unused email.  
>>> → registration succeeds
> >
>>> **Scenario**: the one where a person tries to register with an already used email.  
>>> → registration fails
> >
>> **Rule**: First name, Last name and Email are mandatory for registration
>>> **Scenario**: the one where a person tries to register with first name, last name and email.  
>>> → registration succeeds
> >
>>> **Scenario**: the one where a person tries to register without one of the mandatory fields fields.  
>>> → registration fails

Nice, looks like we have something close to [executable specifications](https://johnfergusonsmart.com/bdd-treaties/). Let's get a little bit more technical.

## System Tests

The team [discovered](https://leanpub.com/bddbooks-discovery) some examples of what the application is suppoed to do at a very high level. From here we will try to [formulate](https://leanpub.com/bddbooks-formulation) these examples into acceptance criteria using the [Gherkin](https://cucumber.io/docs/gherkin/reference/) syntax, and we'll do that while writing automated tests with [Cucumber](https://cucumber.io/).

There are many steps involved to make that happen, but let's first set up our project before we start.

For the service applications I will be using [Spring Boot](https://spring.io/projects/spring-boot) via [Maven](https://maven.apache.org/download.cgi) with [Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) - and before I get shamed into oblivion, the only reason why I am using this version is because on the background I will also be experimenting with [Azure Pipelines](https://docs.microsoft.com/en-us/azure/devops/pipelines/get-started/what-is-azure-pipelines?view=azure-devops), which (by the time of this writing) only supports up to 11 out of the box. Feel free to use any other version you like.

Ok, so let's head over to [Spring Initializr](https://start.spring.io/) and create a Maven Java project. I will use Spring Boot version **2.3.1** with metadata as follows:

``` yml
Group: com.lesson-scheduler
Artifact: user-service
Name: user-service
Description: Lesson Scheduler's user service
Package name: com.lessonscheduler.user
Packaging: Jar
Java: 11
Dependencies:
    Spring Boot DevTools
    Lombok 
    Spring Configuration Processor
    Spring Web
```

The only necessary dependencies to follow this guide are Lombok and Spring Web. The others are just tools to make developers' lives easier. We will be using many other Spring dependencies, but we'll add them when we need them - so it's easier to understand why they are there.  
Also, because not everybody is familiar with the Spring framework, whenever there is a new annotation introduced in this series I will try to leave a brief explanation of what it does and maybe why use it. The how is beyond our scope.

Generate, download and extract the project. Open it on your favorite IDE.  
Now head over to your `pom.xml` file and add the following dependencies:

``` xml
<properties>
    <cucumber.version>6.2.2</cucumber.version>
    <rest-assured.version>4.3.1</rest-assured.version>
    <maven.failsafe.plugin.version>2.22.0</maven.failsafe.plugin.version>
</properties>
...
<dependencies>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-java</artifactId>
        <version>${cucumber.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-junit</artifactId>
        <version>${cucumber.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency><!-- so Cucumber works with JUnit 5 -->
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-junit-platform-engine</artifactId>
        <version>${cucumber.version}</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <version>${rest-assured.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        ...
        <plugin>
            <artifactId>maven-failsafe-plugin</artifactId>
            <version>${maven.failsafe.plugin.version}</version>
            <configuration>
                <includes>
                    <include>**/CucumberRunner.java</include>
                </includes>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>integration-test</goal>
                        <goal>verify</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Having the dependencies to run tests with Cucumber, we can now create the `user_registration.feature` file under `src/test/resources/com/lessonscheduler/user/e2e` with the content:

``` Gherkin
Feature: User registration

  Rule: Email in User registration must be unique

    Scenario: Successful registration with unused email
      When Abel attempts to register with his email
      Then he should see that the registration was successful

    Scenario: Failed registration with used email
      Given that Cain already registered with his email
      When he attempts to register with the same email
      Then he should see that the registration failed

  Rule: First name, Last name and Email are mandatory for registration

    Scenario: Successful registration with all mandatory fields
      When Abel attempts to register using first and last names and email
      Then he should see that the registration was successful

    Scenario Outline: Failed registration with missing mandatory field
      When Cain attempts to register without <mandatory_field>
      Then he should see that the registration failed

      Examples:
        | mandatory_field |
        | first name      |
        | last name       |
        | email           |
```

Now, to run these scenarios with Maven we need a test class pointing to Cucumber. In the `pom.xml` we already included the failsafe plugin configured to look for this class. So let's create a `CucumberRunner.java` under the package `com.lessonscheduler.user.e2e` in the test folder, and the content is this:

``` java
import io.cucumber.junit.platform.engine.Cucumber;

@Cucumber
public class CucumberRunner {
}
```

And the Cucumber options in a `junit-platform.properties` file under `src/test/resources` (otherwise maven will not picj them up) with:

``` properties
cucumber.glue=com/lessonscheduler/user/e2e/steps
cucumber.plugin=pretty, html:target/cucumber/cucumberReport.html
```

And finally, to know what to do when it gets to the steps in the feature file, Cucumber needs a steps definition class. Lets create the `RegistrationStepDefs.java` under `com.lessonscheduler.user.e2e.steps`. Here is part of it. The rest is in the repo:

``` java
public class RegistrationStepDefs {

    private static final String SERVICE_BASE_URL = "http://localhost:8081/api/v1";
    Persona person;
    Response response;

    @Given("that {word} already registered with his/her email")
    @When("{word} attempts to register using first and last names and email")
    @When("{word} attempts to register with his/her email")
    public void person_attempts_to_register_with_valid_data(String personName) {
        person = new Persona(personName);

        response =
                given().auth().basic("admin", "password")
                       .contentType(JSON)
                       .body(person.getValidRegistrationBody().toString())
                       .when().post(SERVICE_BASE_URL + "/users");
    }
    ...
}
```
Note that the `SERVICE_BASE_URL` is where we expect the service to be running when it's possible to run the tests against it - we can change it later if needed.

I am using a custom `Persona` object so that generating valid and invalid `json` bodies is abstracted away. The `response` is also a separated so it can be checked in different steps.

There could be other layers for these high level steps, but for the sake of brevity let's keep it like that. The scenarios can now be run, so we will know if the user registration feature works once we have something running.

You can find the full code for this part here.
Next, we start things bottom up.