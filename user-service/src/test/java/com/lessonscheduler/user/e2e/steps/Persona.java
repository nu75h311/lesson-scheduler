package com.lessonscheduler.user.e2e.steps;

import static java.lang.System.currentTimeMillis;

import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

@Getter
public class Persona {

    private final String firstName;
    private final String lastName;
    private final String email;
    private JSONObject validRegistrationBody;

    Persona(String name) {
        this.firstName = name;
        this.lastName = name + "LastName";
        this.email = "email" + currentTimeMillis() + "@example.com";
        try {
            this.validRegistrationBody = new JSONObject()
                    .put("firstName", firstName)
                    .put("lastName", lastName)
                    .put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getInvalidRegistrationBodyWithout(String missingField) {
        validRegistrationBody.remove(missingField);
        return validRegistrationBody;
    }
}
