Feature: User registration

  Rule: Email in User registration must be unique

    Scenario: Successful registration with unused email
      When Abel attempts to register with his email
      Then he should see that the registration was successful

    Scenario: Failed registration with used email
      Given that Cain already registered with his email
      When he attempts to register with the same email
      Then he should see that the registration failed for existing email

  Rule: First name, Last name and Email are mandatory for registration

    Scenario: Successful registration with all mandatory fields
      When Abel attempts to register using first and last names and email
      Then he should see that the registration was successful

    Scenario Template: Failed registration with missing mandatory field
      When Cain attempts to register without <mandatory_field>
      Then she should see that the registration failed for missing field

      Examples:
        | mandatory_field |
        | firstName       |
        | lastName        |
        | email           |