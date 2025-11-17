package tests;

import base_urls.BaseApi;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.ApiUtil;

import java.util.HashMap;

public class US003_Logout {

    @Test(description = "[US03_TC001] Verify successful logout")
    public void testSuccessfulLogout() {

        // Ensure token exists before logout
        String tokenBefore = BaseApi.getToken();
        Assert.assertNotNull(tokenBefore, "Token should exist before logout!");

        // Send POST /logout
        Response response = ApiUtil.post("/logout", new HashMap<>());

        // Validate status code
        Assert.assertEquals(response.statusCode(), 200, "Status code mismatch!");

        // Validate message
        String message = response.jsonPath().getString("message");
        Assert.assertTrue(message.contains("Successfully"), "Logout message mismatch!");

        System.out.println("Logout Response: " + message);

        // Remove token so next calls don’t use old/expired token
        BaseApi.clearToken();

        System.out.println("Token after logout: " + BaseApi.getToken());

    }



}
