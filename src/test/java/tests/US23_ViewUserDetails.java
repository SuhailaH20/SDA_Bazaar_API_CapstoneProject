package tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.ApiUtil;
import utilities.ConfigReader;
import static org.hamcrest.Matchers.*;



public class US23_ViewUserDetails {

    // TC001 - Admin can retrieve specific user details
    @Test
    public void US23_TC001_getUserById() {

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());


        ApiUtil.setToken(token);

        int userId = 352;
        Response response = ApiUtil.get("/users/" + userId);
        Assert.assertEquals(response.statusCode(), 200);

        System.out.println("-> TC01: Verify admin can retrieve user details successfully");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Admin successfully retrieved details for user ID: " + userId);
        response.prettyPrint();
        System.out.println("SUCCESS: retrieved details for user ID:" + userId);

    }


    // TC002 - Validate structure of user details
    @Test
    public void US23_TC002_verifyUserStructure() {

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        int userId = 352;
        Response response = ApiUtil.get("/users/" + userId);

        Assert.assertEquals(response.statusCode(), 200);

        // Validate required fields
        response.then()
                .body("id", notNullValue())
                .body("name", notNullValue())
                .body("email", notNullValue())
                .body("role", notNullValue())
                .body("created_at", notNullValue())
                .body("updated_at", notNullValue());

        System.out.println("-> TC02: Verify response contains all required user fields");
        System.out.println("status code: " + response.statusCode());
        System.out.println("User ID " + userId + " contains all required fields:");
        response.prettyPrint();
        System.out.println("SUCCESS: User object contains all required fields.");
    }


    // TC003 - 404 for non-existing user
    @Test
    public void US23_TC003_userNotFound() {

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());


        ApiUtil.setToken(token);
        int invalidId = 999999; // ID not in database

        Response response = ApiUtil.get("/users/" + invalidId);
        Assert.assertEquals(response.statusCode(), 404);

        System.out.println("-> TC03: Verify 404 is returned for non-existing user ID");
        System.out.println("status code: " + response.statusCode());
        System.out.println("User not found for ID: " + invalidId );
    }


    // TC004 - Request without token returns 401
    @Test
    public void US23_TC004_noTokenUnauthorized() {

        int userId = 355;
        Response response = ApiUtil.getRequestSpec().get("/users/" + userId);
        Assert.assertEquals(response.statusCode(), 401);

        System.out.println("-> TC04: Verify request without token returns 401");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Unauthorized access blocked -> No token provided.");
    }


    // TC005 - Customer should NOT access user details (BUG)
    @Test
    public void US23_BUGTC005_customerForbidden() {

        String customerToken = ApiUtil.loginAndGetToken(
                ConfigReader.getCustomerEmail(),
                ConfigReader.getDefaultPassword());

        int userId = 355;
        Response response = ApiUtil.getWithAuth("/users/" + userId, customerToken);

        if (response.statusCode() == 200) {

            System.out.println("-> TC05: Verify non-admin users cannot access user details");
            System.out.println("status code: " + response.statusCode());
            System.out.println("BUG: Customer SHOULDN'T be able to access user details!");
            response.prettyPrint();
            Assert.fail("BUG FOUND: Customer can access user details");
        }

        // If the API gets fixed
        Assert.assertEquals(response.statusCode(), 403);
        System.out.println("-> TC05: Verify non-admin users cannot access user details");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Customer correctly forbidden from accessing user details");
    }
}
