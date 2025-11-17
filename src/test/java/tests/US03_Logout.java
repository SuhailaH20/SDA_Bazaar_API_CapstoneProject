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

}
