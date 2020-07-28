---
layout: post
title: "PART 2: The model layer"
date: 2020-07-20 21:01:00 +0200
tags: unit_tests
author: adriano
---

### On this page

1. Define how the [User entity](#user-object) will look in the database
2. Check if [JSON <-> Object](#serialization-tests) works
3. Define [mandatory fields](#validating-mandatory-fields) for object creation
4. [Ignore](#ignoring-extra-fields) extra fields sent for parsing

## User Object

In the last part we created some high level tests that assume the user service is deployed and running somewhere. But we don't even have the service yet, let alone have it deployed! Let's start by assuming that the table structure of our users database was defined to be like the following:

| User property | Type             |
| ------------- | ---------------- |
| id            | UUID             |
| first_name    | VARCHAR NOT NULL |
| last_name     | VARCHAR NOT NULL |
| email         | VARCHAR UNIQUE   |

We need to create an object that will represent the users in our code. To make things nicely separated, we'll create a package named `com.lessonscheduler.user.models` in both `main` and `test` folders.  
Now, it doesn't make much sense to have unit tests for getters and setters of simple objects, but knowing that the service will be sending and receiving the User object in JSON format, it does make sense to have tests to check the serialization and deserialization of this object, so when for example an update of the Jackson library comes, we can be sure that the serialization is (or is not) the issue.

## Serialization Tests

So now in [TDD](https://martinfowler.com/bliki/TestDrivenDevelopment.html) fashion, let's create the `UserJsonSerializationTests` class in the `models` test package. Here we'll make user of spring-boot's JacksonTester - which attempts the parsing of object to json and vice-versa -, so there is the need of a little setup `@BeforeEach` test:

```java
class UserJsonSerializationTests {

    private JacksonTester<User> json;

    private static final String JSON_VALID_USER =
        "{\"firstName\" : \"John\",\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\"}";

    @BeforeEach
    public void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);
    }

    @Test @DisplayName("Parse valid User json to object")
    void testUserValidParseJson() throws IOException {
        User user = new User.UserBuilder().firstName("John")
                                          .lastName("Doe")
                                          .email("john.doe@example.com")
                                          .build();

        assertThat(this.json.parse(JSON_VALID_USER)).isEqualTo(user);
    }

    @Test @DisplayName("Marshall User object to json")
    void testUserValidMarshallObjectToJson() throws IOException {
        User user = new User.UserBuilder().firstName("John")
                                          .lastName("Doe")
                                          .email("john.doe@example.com")
                                          .build();

        assertThat(this.json.write(user)).isEqualTo(JSON_VALID_USER);
    }
}
```

Now we have one test for JSON to Object, and one for Object to JSON. Let's make them pass by creating the `User` class in the `models` main package:

Create the `User` class.

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @EqualsAndHashCode.Exclude
    private UUID id;

    private String firstName;
    private String lastName;
    private String email;
}
```

Note the Lombok annotations to keep the object small and clean. The `@Data` annotation contains the `@Getter`, `@Setter`, `@EqualsAndHashCode` and [others](https://projectlombok.org/features/Data).

## Validating Mandatory fields

The first two tests should now pass. Let's add one to validate a missing mandatory field:

```java
private static final String JSON_MISSING_FIRST_NAME_USER =
            "{\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\"}";

@Test  @DisplayName("Validate that firstName is required")
void testUserMissingFirstNameField() {
    assertThrows(MismatchedInputException.class, () -> this.json.parse(jsonMissingFirstNameUser));
}
```

> Note that in the actual repository I use JUnit5's `@ParameterizedTest`, which requires a bit of tweaking to have a nice `@DisplayName`, but then you only need one test code for every mandatory field.

To make the new test(s) pass, add the following annotation to the `User` class:

```java
@JsonProperty(value = "firstName", required = true)
private String firstName;

@JsonProperty(value = "lastName", required = true)
private String lastName;

@JsonProperty(value = "email", required = true)
private String email;
...
```

...but because we are using Lombok for the constructor, in order for these field properties to be picked up Lombok needs the following in a `lombok.config` file at the root of `user-service`:

```properties
lombok.anyConstructor.addConstructorProperties=true
config.stopBubbling=true
```

This way the `required` flag is passed on to the Lombok constructor so `Jackson` recognizes it when using it.

## Ignoring extra fields

Now, one last test to make sure that if anything else is sent with the JSON to be parsed as the User object, the extra information will be ignored:

```java
private static final String JSON_USER_EXTRA_FIELDS =
            "{\"firstName\" : \"John\",\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\",\"extraField\" : \"extraValue\"}";

@Test @DisplayName("Ignore extra fields in json to be parsed")
void testUserIgnoreExtraField() throws IOException {
    User user = new User.UserBuilder().firstName("John")
                                        .lastName("Doe")
                                        .email("john.doe@example.com")
                                        .build();

    assertThat(this.json.parse(JSON_USER_EXTRA_FIELDS)).isEqualTo(user);
}
```

And finally, to make this one pass, simply add the following annotation to the `User` class:

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
...
```

You can find the full code for this part [here](https://github.com/nu75h311/lesson-scheduler/tree/part-02-the-model-layer).
Next, we leverage Spring.
