---
layout: post
title: "PART 2: The model layer"
date: 2020-07-20 21:01:00 +0200
tags: unit_tests
author: adriano
---

## On this page

1. 

## Object

| User property | Type             |
| ------------- |------------------|
| id            | UUID             |
| first_name    | VARCHAR NOT NULL |
| last_name     | VARCHAR NOT NULL |
| email         | VARCHAR UNIQUE   |

Add `spring-boot-starter-data-jpa` dependency.

Create package `models`.

Create `UserJsonSerializationTests`. Make user of spring-boot's JacksonTester.

```java
public class UserJsonSerializationTests {

    private JacksonTester<User> json;

    private final String jsonValidUser =
            "{\"firstName\" : \"John\",\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\"}";

    @BeforeEach
    public void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);
    }

    @Test @DisplayName("Parse valid User json to object")
    public void testValidUserParseJson() throws IOException {
        User user = new User.UserBuilder().firstName("John").lastName("Doe").email("john.doe@example.com").build();

        assertThat(this.json.parse(jsonValidUser)).isEqualTo(user);
    }

    @Test @DisplayName("Marshall User object to json")
    public void testValidUserMarshallObjectToJson() throws IOException {
        User user = new User.UserBuilder().firstName("John").lastName("Doe").email("john.doe@example.com").build();

        assertThat(this.json.write(user)).isEqualTo(jsonValidUser);
    }
}
```

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

add

```java
private final String jsonMissingFirstNameUser =
            "{\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\"}";

@Test  @DisplayName("Validate that firstName is required")
public void testUserMissingFirstNameField() {
    assertThrows(MismatchedInputException.class, () -> this.json.parse(jsonMissingFirstNameUser));
}
```
in the actual code I use JUnit5's `@ParameterizedTest`, which requires a bit of tweaking to have a nice display name, but then you only need one test code for every mandatory field.


fix by adding:

```java
@JsonProperty(value = "firstName", required = true)
private String firstName;

@JsonProperty(value = "lastName", required = true)
private String lastName;

@JsonProperty(value = "email", required = true)
private String email;
...
```
but to work with this annotation, Lombok needs this in a `lombok.config` file at the root:
```properties
lombok.anyConstructor.addConstructorProperties=true
config.stopBubbling=true
```

Ignore extra info:
```java
private final String jsonUserExtraFields =
        "{\"firstName\" : \"John\",\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\",\"extraField\" : \"extraValue\"}";

@Test @DisplayName("Ignore extra fields in json to be parsed")
public void testUserIgnoreExtraField() throws IOException {
    User user = new User.UserBuilder().firstName("John").lastName("Doe").email("john.doe@example.com").build();

    assertThat(this.json.parse(jsonUserExtraFields)).isEqualTo(user);
}
```
simply add
```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
...
```