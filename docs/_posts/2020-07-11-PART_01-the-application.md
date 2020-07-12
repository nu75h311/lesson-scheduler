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
>>
>>> **Scenario**: the one where a person tries to register with an already used email.  
>>> → registration fails
>>
>> **Rule**: First name, Last name and Email are mandatory for registration
>>> **Scenario**: the one where a person tries to register with first name, last name and email.  
>>> → registration succeeds
>>
>>> **Scenario**: the one where a person tries to register without one of the mandatory fields fields.  
>>> → registration fails

Nice, looks like we have something close to [executable specifications](https://johnfergusonsmart.com/bdd-treaties/). Let's get a little bit more technical.


## System Tests

The team [discovered](https://leanpub.com/bddbooks-discovery) some examples of what the application is suppoed to do at a very high level. From here we will try to [formulate](https://leanpub.com/bddbooks-formulation) these examples into acceptance criteria using the [Gherkin](https://cucumber.io/docs/gherkin/reference/) syntax, and we'll do that while writing automated tests with [Cucumber](https://cucumber.io/).

There are many steps involved to make that happen, but let's first set up our project before we start.

For the service applications I will be using [Spring Boot](https://spring.io/projects/spring-boot) via [Maven](https://maven.apache.org/download.cgi) with [Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) - and before I get shamed into oblivion, the only reason why I am using this version is because on the background I will also be experimenting with [Azure Pipelines](https://docs.microsoft.com/en-us/azure/devops/pipelines/get-started/what-is-azure-pipelines?view=azure-devops), which (by the time of this writing) only supports up to 11 out of the box. Feel free to use any other version you like.

Ok, so let's head over to [Spring Initializr](https://start.spring.io/) and create a Maven Java project. I will use Spring Boot version **2.3.1** with metadata as follows:
```yml
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

Generate, download and extract the project. Open it on your favorite IDE.