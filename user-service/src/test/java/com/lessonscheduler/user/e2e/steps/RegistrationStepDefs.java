package com.lessonscheduler.user.e2e.steps;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.TEXT;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

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

    @When("(s)he attempts to register with the same email")
    public void person_attempts_to_register_with_same_email() {
        response =
                given().auth().basic("admin", "password")
                       .contentType(JSON)
                       .body(person.getValidRegistrationBody().toString())
                       .when().post(SERVICE_BASE_URL + "/users");
    }

    @Then("(s)he should see that the registration was successful")
    public void should_see_that_the_registration_was_successful() {
        response.then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(JSON)
                .body("id", notNullValue())
                .body("firstName", equalTo(person.getFirstName()))
                .body("lastName", equalTo(person.getLastName()))
                .body("email", equalTo(person.getEmail()));
    }

    @When("{word} attempts to register without {word}")
    public void person_attempts_to_register_without_a_field(String personName, String missingField) {
        person = new Persona(personName);

        response =
                given().auth().basic("admin", "password")
                       .contentType(JSON)
                       .body(person.getInvalidRegistrationBodyWithout(missingField).toString())
                       .when().post(SERVICE_BASE_URL + "/users");
    }

    @Then("(s)he should see that the registration failed for existing email")
    public void should_see_that_the_registration_failed_for_existing_email() {
        response.then()
                .log().ifValidationFails()
                .statusCode(409)
                .contentType(TEXT)
                .body(containsString("Email '" + person.getEmail() + "' already registered."));
    }

    @Then("(s)he should see that the registration failed for missing field")
    public void should_see_that_the_registration_failed_for_missing_field() {
        response.then()
                .log().ifValidationFails()
                .statusCode(400)
                .contentType(TEXT)
                .body(containsString("There are mandaory fields missing"));
    }

}
