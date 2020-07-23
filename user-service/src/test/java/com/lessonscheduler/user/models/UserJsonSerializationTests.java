package com.lessonscheduler.user.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.json.JacksonTester;

@Tag("model")
@DisplayName("User model layer tests - JSON serialization")
class UserJsonSerializationTests {

    private static final String JSON_VALID_USER =
            "{\"firstName\" : \"John\",\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\"}";
    private static final String JSON_MISSING_FIRST_NAME_USER =
            "{\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\"}";
    private static final String JSON_MISSING_LAST_NAME_USER =
            "{\"firstName\" : \"John\",\"email\" : \"john.doe@example.com\"}";
    private static final String JSON_MISSING_EMAIL_USER =
            "{\"firstName\" : \"John\",\"lastName\" : \"Doe\"}";
    private static final String JSON_USER_EXTRA_FIELDS =
            "{\"firstName\" : \"John\",\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\",\"extraField\" : \"extraValue\"}";
    private JacksonTester<User> json;

    @BeforeEach
    public void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    @DisplayName("Parse valid User json to object")
    void testUserValidParseJson() throws IOException {
        User user = new User.UserBuilder().firstName("John")
                                          .lastName("Doe")
                                          .email("john.doe@example.com")
                                          .build();

        assertThat(this.json.parse(JSON_VALID_USER)).isEqualTo(user);
    }

    @Test
    @DisplayName("Marshall User object to json")
    void testUserValidMarshallObjectToJson() throws IOException {
        User user = new User.UserBuilder().firstName("John")
                                          .lastName("Doe")
                                          .email("john.doe@example.com")
                                          .build();

        assertThat(this.json.write(user)).isEqualTo(JSON_VALID_USER);
    }

    @DisplayName("Throw exception when parsing User json with missing fields")
    @ParameterizedTest(name = "Validate that ''{0}'' is required")
    @CsvSource({"firstName, " + JSON_MISSING_FIRST_NAME_USER,
            "lastName, " + JSON_MISSING_LAST_NAME_USER,
            "email, " + JSON_MISSING_EMAIL_USER})
    void testUserMissingMandatoryField(ArgumentsAccessor arguments) {
        // this trouble is so the display names of the tests show nicely
        String incompleteJsonUser = arguments.getString(1) + "," + arguments.getString(2);
        assertThrows(MismatchedInputException.class, () -> this.json.parse(incompleteJsonUser));
    }

    @Test
    @DisplayName("Ignore extra fields in json to be parsed")
    void testUserIgnoreExtraField() throws IOException {
        User user = new User.UserBuilder().firstName("John")
                                          .lastName("Doe")
                                          .email("john.doe@example.com")
                                          .build();

        assertThat(this.json.parse(JSON_USER_EXTRA_FIELDS)).isEqualTo(user);
    }
}
