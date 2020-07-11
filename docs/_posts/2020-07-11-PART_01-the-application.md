---
layout: post
title: "PART 1: The application"
date: 2020-07-11 16:21:59 +0200
tags: business
author: adriano
---

## BDD

In [Behavior Driven Development](https://cucumber.io/docs/bdd/) fashion, let's start by trying to describe this application via an elevator pitch:

> It's an app where every user can create lessons - with defined start and end date/time - for which (s)he will be the teacher, and every other user can sign up for that lesson as a student.

Ok, that could have come from a business analyst describing the app from 10,000 feet. From this altitude, let's imagine that there were some discussions and some very high level Epics came to existence:

- Anyone can register with an email
- Any user can create lessons
- Any user can sign up for other users' lessons
- Users should not be able to give/attend more than one lesson at the same time

> *Note that we will only deal with the scheduling requirements. How these lessons are given is irrelevant to this exercise.*

Now the team can have some [Example Mapping](https://cucumber.io/blog/bdd/example-mapping-introduction/) sessions to come up with the scenarios that specify these epics.