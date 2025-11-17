package tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.ApiUtil;

public class US04_Profile {

    @Test(description = "[US04_TC001] Retrieve current user profile with valid token")
    public void testRetrieveCurrentUserProfile() {

        // Call GET /profile using ApiUtil (BaseApi adds token automatically)
        Response response = ApiUtil.get("/me");

        // Validate status
        Assert.assertEquals(response.statusCode(), 200, "Expected status 200 for profile");

        // Validate user fields exist
        Assert.assertNotNull(response.jsonPath().getString("id"), "User ID should be present");
        Assert.assertNotNull(response.jsonPath().getString("name"), "User name should be present");
        Assert.assertNotNull(response.jsonPath().getString("email"), "User email should be present");

        System.out.println("Profile Response: " + response.asString());
    }

}
