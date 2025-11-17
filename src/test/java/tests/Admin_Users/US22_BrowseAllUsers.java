package tests.Admin_Users;



import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.ApiUtil;
import utilities.ConfigReader;
import static org.hamcrest.Matchers.*;

public class US22_BrowseAllUsers {


    // TC001 - Admin can retrieve all users
    @Test
    public void US22_TC001_getAllUsers() {

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        Response response = ApiUtil.get("/users");
        Assert.assertEquals(response.statusCode(), 200);

        System.out.println("-> TC01: Admin can retrieve all users");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Admin successfully retrieved all users:");
        response.prettyPrint();
        System.out.println("SUCCESS: All users retrieved successfully.");}



    // TC002 - Validate user object structure
    @Test
    public void US22_TC002_verifyUserStructure() {

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        Response response = ApiUtil.get("/users");
        Assert.assertEquals(response.statusCode(), 200);

        response.then().body("$", hasSize(greaterThan(0)));
        int count = response.jsonPath().getList("$").size();
        for (int i = 0; i < count; i++) {
            response.then()
                    .body("[" + i + "].id", notNullValue())
                    .body("[" + i + "].name", notNullValue())
                    .body("[" + i + "].email", notNullValue())
                    .body("[" + i + "].role", notNullValue())
                    .body("[" + i + "].created_at", notNullValue())
                    .body("[" + i + "].updated_at", notNullValue());
        }

        System.out.println("-> TC02: Validate user object structure");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verified that all " + count + " users contain required fields:");
        response.prettyPrint();
        System.out.println("SUCCESS: All user objects contain all required fields.");}



    // TC003 - Users list is not empty
    @Test
    public void US22_TC003_usersListNotEmpty() {

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());


        ApiUtil.setToken(token);
        Response response = ApiUtil.get("/users");
        Assert.assertEquals(response.statusCode(), 200);

        int count = response.jsonPath().getList("$").size();
        Assert.assertTrue(count > 0);

        System.out.println("-> TC03: Users list is not empty");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Users list is NOT empty. Total users: " + count);
        response.prettyPrint();
        System.out.println("SUCCESS: User list contains data.");
    }


    // TC004 - Unauthorized (no token)
    @Test
    public void US22_TC004_unauthorizedAccess_NoToken() {

        Response response = ApiUtil.getRequestSpec().get("/users");
        Assert.assertEquals(response.statusCode(), 401);

        System.out.println("-> TC04: Verify unauthorized access is rejected");
        System.out.println("status code: " + response.statusCode());
        System.out.println("No token provided -> Unauthorized access blocked correctly.");

    }


    // TC005 - Invalid token
    @Test
    public void US22_TC005_invalidToken() {

        Response response = ApiUtil.getWithAuth("/users", "invalid_token");
        Assert.assertEquals(response.statusCode(), 401);

        System.out.println("-> TC05: Verify invalid token is rejected");
        System.out.println("status code: " + response.statusCode());
        System.out.println("token is invalid -> Invalid token correctly rejected");
    }


    // TC006 - Customer should NOT access users (expected bug)
    @Test
    public void US22_BUGTC006_customerForbidden() {

        String customerToken = ApiUtil.loginAndGetToken(
                ConfigReader.getCustomerEmail(),
                ConfigReader.getDefaultPassword());

        Response response = ApiUtil.getWithAuth("/users", customerToken);

        // API SHOULD return 403
        // BUT because of bug → it returns 200
        if (response.statusCode() == 200) {

            System.out.println("-> TC06: Verify non-admin users cannot access users list");
            System.out.println("status code: " + response.statusCode());
            System.out.println("BUG: Customer SHOULD NOT access to users!");
            response.prettyPrint();
            Assert.fail("BUG FOUND: Non-admin user can access /users.");
        }

        // If API is fixed
        Assert.assertEquals(response.statusCode(), 403);
        System.out.println("-> TC06: Verify non-admin users cannot access users list");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Customer correctly blocked.");
    }}
