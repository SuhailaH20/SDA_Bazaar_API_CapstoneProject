package tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.ApiUtil;

import java.util.HashMap;

public class US03_Logout {

    @Test(description = "[US03_TC001] Verify successful logout")
    public void testSuccessfulLogout() {

        System.out.println(ApiUtil.getToken());

        // If no token exists, login first
        if (ApiUtil.getToken() == null) {
            String token = ApiUtil.loginAndGetToken("sara@example.com", "Pass123!");
            ApiUtil.setToken(token);
        }
        System.out.println(ApiUtil.getToken());

        // Ensure token exists before logout
        String tokenBefore = ApiUtil.getToken();
        Assert.assertNotNull(tokenBefore, "Token should exist before logout!");

        // Send POST /logout
        Response response = ApiUtil.post("/logout", new HashMap<>());

        // Validate status code
        Assert.assertEquals(response.statusCode(), 200, "Status code mismatch!");

        // Validate message
        String message = response.jsonPath().getString("message");
        Assert.assertTrue(message.contains("Successfully"), "Logout message mismatch!");

        System.out.println("Logout Response: " + message);
        System.out.println(ApiUtil.getToken());

        // Remove token so next calls don’t use old token
        ApiUtil.clearToken();
        System.out.println(ApiUtil.getToken());

    }


    @Test(description = "[US03_TC002] Verify logout without token returns Unauthenticated")
    public void testLogoutWithoutToken() {

        ApiUtil.clearToken();
        Assert.assertNull(ApiUtil.getToken(), "Token should NOT exist before logout!");

        Response response = ApiUtil.post("/logout", new HashMap<>());

        Assert.assertEquals(response.statusCode(), 401);
        Assert.assertEquals(response.jsonPath().getString("message"), "Unauthenticated.");
    }

    @Test(description = "[US03_TC003] Validate logout with invalid token format")
    public void testLogoutWithInvalidTokenFormat() {

        // Set an invalid token (malformed)
        ApiUtil.setToken("12345-invalid-token");

        Response response = ApiUtil.post("/logout", new HashMap<>());

        Assert.assertEquals(response.statusCode(), 401, "Invalid token should return 401");
        Assert.assertTrue(response.asString().contains("Unauthenticated"), "Expected Unauthenticated response");
    }

    @Test(description = "[US03_TC004] Validate logout twice returns Unauthenticated")
    public void testLogoutTwice() {

        // Step 1 — login
        String token = ApiUtil.loginAndGetToken("sara@example.com", "Pass123!");
        ApiUtil.setToken(token);

        // Step 2 — first logout (expected success)
        Response first = ApiUtil.post("/logout", new HashMap<>());
        Assert.assertEquals(first.statusCode(), 200);

        // Step 3 — second logout with same token (should be rejected)
        Response second = ApiUtil.post("/logout", new HashMap<>());
        Assert.assertEquals(second.statusCode(), 401);
        Assert.assertTrue(second.asString().contains("Unauthenticated"));

        ApiUtil.clearToken();
    }

    @Test(description = "[US03_TC005] Validate logout using token from another user")
    public void testLogoutWithDifferentUserToken() {

        // Login as User A
        String tokenA = ApiUtil.loginAndGetToken("sara@example.com", "Pass123!");
        ApiUtil.setToken(tokenA);
        ApiUtil.post("/logout", new HashMap<>()); // logout A

        // Login as User B
        String tokenB = ApiUtil.loginAndGetToken("customer@sda.com", "Password.12345");

        // Try to logout using User A token (should fail)
        ApiUtil.setToken(tokenA);

        Response response = ApiUtil.post("/logout", new HashMap<>());

        Assert.assertEquals(response.statusCode(), 401);
        Assert.assertTrue(response.asString().contains("Unauthenticated"));

        ApiUtil.setToken(tokenB); // cleanup
        ApiUtil.post("/logout", new HashMap<>());
        ApiUtil.clearToken();
    }


}
