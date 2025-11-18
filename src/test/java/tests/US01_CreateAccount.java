package tests;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.ApiUtil;

import java.util.HashMap;
import java.util.Map;

public class US01_CreateAccount {

    Faker faker = new Faker();

    @Test(description = "[US01_TC001] Verify successful user registration")
    public void testSuccessfulRegistration() {

        // Dynamic test data 1
        String randomEmail = faker.internet().emailAddress();
        String randomName = faker.name().firstName();

        // Request body
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", randomName);
        payload.put("email", randomEmail);
        payload.put("password", "Pass123!");
        payload.put("password_confirmation", "Pass123!");

        // Send POST /register
        Response response = ApiUtil.post("/register", payload);

        // Assertions
        Assert.assertEquals(response.statusCode(), 201, "Status code mismatch!");
        Assert.assertEquals(response.jsonPath().getString("status"), "success");

        Assert.assertEquals(response.jsonPath().getString("user.name"), randomName);
        Assert.assertEquals(response.jsonPath().getString("user.email"), randomEmail);

        System.out.println("Registration completed with email: " + randomEmail);
    }

    @Test(description = "[US01_TC002] Validate duplicate email registration")
    public void testDuplicateEmailRegistration() {

        // Fixed email that already exists
        String existingEmail = "sara@example.com";

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Sara");
        payload.put("email", existingEmail);
        payload.put("password", "Pass123!");
        payload.put("password_confirmation", "Pass123!");

        // Send POST /register
        Response response = ApiUtil.post("/register", payload);

        // Assertions
        Assert.assertEquals(response.statusCode(), 422, "Status code should be 422 for duplicate email");

        // Validate the message
        String errorMessage = response.jsonPath().getString("email[0]");
        Assert.assertEquals(errorMessage, "The email has already been taken.",
                "Error message mismatch!");

        System.out.println("Duplicate email validation successful for: " + existingEmail);
    }

    @Test(description = "[US01_TC003] Validate missing required fields")
    public void testMissingRequiredFields() {

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Sara");
        payload.put("email", "sara@example.com");
        // password intentionally omitted

        // Send POST /register
        Response response = ApiUtil.post("/register", payload);

        // Assertions
        Assert.assertEquals(response.statusCode(), 422, "Status code should be 422 for missing fields");

        String errorMessage = response.jsonPath().getString("password[0]");
        Assert.assertEquals(errorMessage, "The password field is required.",
                "Error message mismatch!");

        System.out.println("Validation for missing password field passed.");
    }


    @Test(description = "[US01_TC004] Validate invalid email format")
    public void testInvalidEmailFormat() {

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Sara");
        payload.put("email", "saraexample.com");  // invalid email
        payload.put("password", "Pass123!");
        payload.put("password_confirmation", "Pass123!");

        // Send POST /register
        Response response = ApiUtil.post("/register", payload);

        // Assertions
        Assert.assertEquals(response.statusCode(), 422, "Status code should be 422 for invalid email");

        String errorMessage = response.jsonPath().getString("email[0]");
        Assert.assertEquals(errorMessage,
                "The email field must be a valid email address.",
                "Error message mismatch!");

        System.out.println("Invalid email format validation passed.");
    }

}
