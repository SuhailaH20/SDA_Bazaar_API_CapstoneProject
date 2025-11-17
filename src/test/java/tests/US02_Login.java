package tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.ApiUtil;

import java.util.HashMap;
import java.util.Map;

public class US02_Login {

    @Test(description = "[US02_TC001] Verify successful login")
    public void testSuccessfulLogin() {

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", "sara@example.com");
        payload.put("password", "Pass123!");

        Response response = ApiUtil.post("/login", payload);

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("status"), "success");
        Assert.assertEquals(response.jsonPath().getString("user.email"), "sara@example.com");

        String token = response.jsonPath().getString("authorisation.token");
        Assert.assertNotNull(token);

        System.out.println("Login successful. Token: " + token);

        // Save the token so logout will work
        ApiUtil.setToken(token);
        System.out.println(ApiUtil.getToken());
    }



    @Test(description = "[US02_TC002] Validate login with invalid credentials")
    public void testInvalidLoginCredentials() {

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", "sara@example.com");
        payload.put("password", "Pass1236");  // wrong password

        // Send POST /login
        Response response = ApiUtil.post("/login", payload);

        // Assertions
        Assert.assertEquals(response.statusCode(), 401, "Expected 401 for invalid credentials");

        String errorMessage = response.jsonPath().getString("error");
        Assert.assertEquals(errorMessage, "Invalid credentials", "Error message mismatch!");

        System.out.println("Invalid credentials test passed.");
    }


}
