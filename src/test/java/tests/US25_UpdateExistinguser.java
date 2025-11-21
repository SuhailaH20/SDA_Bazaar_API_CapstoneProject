package tests;

import base_urls.apiBazaar;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.ConfigReader;
import static io.restassured.RestAssured.given;
import static utilities.ObjectMapperUtils.*;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.BeforeClass;

public class US25_UpdateExistinguser extends apiBazaar {

    public static int userID_1;   // TC001–TC004
    public static int userID_2;   // TC005–TC013

    // Load base body from JSON
    private ObjectNode loadBase(String key) {
        return (ObjectNode) getJsonNode("UpdateUser").get(key).deepCopy();
    }

    // BEFORE CLASS → Get IDs dynamically using email
    @BeforeClass
    public void setup() {

        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
        .get("/users");

        JsonPath json = response.jsonPath();

       userID_1 = json.getInt("find { it.email == 'Rajja@test.com' }.id");
        userID_2 = json.getInt("find { it.email == 'Lena1@test.com' }.id");

        System.out.println("Fetched userID_1 → " + userID_1);
        System.out.println("Fetched userID_2 → " + userID_2);
    }


    // TC001 Update name only
    @Test(priority = 1)
    public void US25_TC001_updateNameOnly() {

        ObjectNode body = loadBase("user1");
        body.put("name", "Rajja");

        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
        .body(body).put("/users/" + userID_1);

        System.out.println("-> TC01: Update name only");
        int status = response.statusCode();
        System.out.println("Status Code: " + status);

        Assert.assertEquals(status, 200, "Expected 200 but got " + status);

        System.out.println("Verify if the user updated in the API:");
        response.prettyPrint();
        System.out.println("User updated successfully");
    }


    // TC002 Update email only (BUG: API returns 500 but email actually updates)
    @Test(priority = 2)
    public void US25BUG_TC002_updateEmailOnly() {

        ObjectNode body = loadBase("user1");
        body.put("email", "Rajja2@test.com");

        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
        .body(body)
        .put("/users/" + userID_1);


        System.out.println("Status Code: " +  response.statusCode());
        if ( response.statusCode() == 500) {
            System.out.println("-> TC02: Update email only");

            // Verify from API if email actually updated
            Response verifyResponse = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
            .get("/users/" + userID_1);

            System.out.println("Verification from API:");
            verifyResponse.prettyPrint();

            String updatedEmail = verifyResponse.jsonPath().getString("email");

            if ("Rajja2@test.com".equals(updatedEmail)) {
                System.out.println("BUG CONFIRMED → Email updated despite 500 error.");
            }

            Assert.fail("BUG: Expected 200 but got 500 while email updated successfully.");
        }

        // =============== EXPECTED BEHAVIOR  ===============
        Assert.assertEquals( response.statusCode(), 200, "Expected status 200 for successful email update");

        // Verify update from API
        Response verify = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
        .get("/users/" + userID_1);

        String updatedEmail = verify.jsonPath().getString("email");
        Assert.assertEquals(updatedEmail, "Ragga2@test.com");

        System.out.println("-> TC02: Update email only");
        System.out.println("Verification from API:");
        verify.prettyPrint();
        System.out.println("Email updated successfully in API.");
    }


    // TC003 BUG – role not updating
    @Test(priority = 3)
    public void US25BUG_TC003_updateRole_bug() {

        ObjectNode body = loadBase("user1");
        body.put("role", "store_management");

        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
        .body(body).put("/users/" + userID_1);

        String updatedRole = response.jsonPath().getString("role");

        if (!"store_management".equals(updatedRole)) {
            System.out.println("Verify if the user updated in the API:");
            response.prettyPrint();
            System.out.println("BUG CONFIRMED → Role not updated correctly.");

            Assert.fail("BUG: role not updated correctly!");
        }

        Assert.assertEquals(updatedRole, "store_management");
        System.out.println("Status Code: " +  response.statusCode() + "-> Role updated successfully to store_management.");
        }


    // TC004 Updating only password → should be rejected with 422
    @Test(priority = 4)
    public void US25_TC004_updatePasswordOnly() {

        ObjectNode body = loadBase("user1");
        body.put("password", "Pass456#");
        body.put("password_confirmation", "Pass123#"); //it's stay with old password confirmation

        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
                .body(body).put("/users/" + userID_1);

        // Assert
        Assert.assertEquals(response.statusCode(), 422);

        // Extract error message
        String errorMessage = response.jsonPath().getString("errors.password[0]");
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Error Message from API → " + errorMessage);
        System.out.println("System correctly rejected password-only update.");
    }


    // TC005 BUG – empty password
    @Test(priority = 5)
    public void US25BUG_TC005_emptyPassword() {

        ObjectNode body = loadBase("user2");
        body.put("password", "");
        body.put("password_confirmation", "Pass123#");

        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
        .body(body).put("/users/" + userID_2);

        if (response.statusCode() == 200) {

         System.out.println("-> TC05: Update with empty password");
         System.out.println("status Code: " + response.statusCode());
         System.out.println("BUG → API accepted empty password!!!");
        Assert.fail("BUG: Empty password accepted!");
        }

        response.then().statusCode(422);
        System.out.println("-> TC05: Update with empty password");
        // Extract error message
        String errorMessage = response.jsonPath().getString("errors.password[0]");
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Error Message from API → " + errorMessage);
        System.out.println("System correctly rejected password empty update.");

    }


    // TC006 empty password confirmation
    @Test(priority = 6)
    public void US25_TC006_emptyPasswordConfirmation() {

        ObjectNode body = loadBase("user2");
        body.put("password", "Pass123#");
        body.put("password_confirmation", "");

        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
                .body(body).put("/users/" + userID_2);


        System.out.println("Status Code: " + response.statusCode());

        Assert.assertEquals(response.statusCode(), 422);
        String msg = response.jsonPath().getString("errors.password[0]");
        System.out.println("Error Message → " + msg);
        System.out.println("System correctly rejected empty password.");
    }



    // TC007 empty name
    @Test(priority = 7)
    public void US25_TC007_EmptyName() {

        ObjectNode body = loadBase("user2");
        body.put("name", "");

        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
        .body(body).put("/users/" + userID_2);


        System.out.println("Status Code: " + response.statusCode());

        Assert.assertEquals(response.statusCode(), 422);

        String msg = response.jsonPath().getString("errors.name[0]");
        System.out.println("Error Message → " + msg);
        System.out.println("System correctly rejected short password.");
    }


    // TC008 missing confirmation
    @Test(priority = 8)
    public void US25_TC008_missingConfirmation() {

        ObjectNode body = loadBase("user2");
        body.put("password", "Pass123#");
        body.put("password_confirmation", "");

        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
                .body(body).put("/users/" + userID_2);


        System.out.println("Status Code: " + response.statusCode());

        Assert.assertEquals(response.statusCode(), 422);

        String msg = response.jsonPath().getString("errors.password[0]");
        System.out.println("Error Message → " + msg);
        System.out.println("System correctly rejected missing confirmation.");
    }


   // TC009 Update with invalid email format
    @Test(priority = 9)
    public void US25_TC009_invalidEmail() {

        ObjectNode body = loadBase("user2");
        body.put("email", "lama1test.com"); // invalid email format

        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
        .body(body).put("/users/" + userID_2);


        System.out.println("Status Code: " + response.statusCode());

        Assert.assertEquals(response.statusCode(), 422);

        String msg = response.jsonPath().getString("errors.email[0]");
        System.out.println("Error Message → " + msg);
        System.out.println("System correctly rejected mismatch confirmation.");
    }



    // TC010 short password during update
    @Test(priority = 10)
    public void US25_TC010_shortPasswordUpdate() {

        ObjectNode body = loadBase("user2");
        body.put("password", "123");
        body.put("password_confirmation", "123");

        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
        .body(body).put("/users/" + userID_2);


        System.out.println("Status Code: " + response.statusCode());

        Assert.assertEquals(response.statusCode(), 422);

        String msg = response.jsonPath().getString("errors.password[0]");
        System.out.println("Error Message → " + msg);
        System.out.println("System correctly rejected short password during update.");
    }


    // TC011 non-admin trying to update user (BUG)
    @Test(priority = 11)
    public void US25BUG_TC011_nonAdminCannotUpdate_bug() {

        ObjectNode body = loadBase("user2");
        body.put("name", "UpdatedByCustomer");

        Response response = given(spec(ConfigReader.getCustomerEmail(), ConfigReader.getDefaultPassword()))
        .body(body).put("/users/" + userID_2);


        System.out.println("Status Code: " + response.statusCode());

        if (response.statusCode() == 200) {
            System.out.println("BUG → Customer was able to update user!");
            response.prettyPrint();
            Assert.fail("BUG: Customer CAN update user info! Expected 403.");
        }

        Assert.assertEquals(response.statusCode(), 403);
        System.out.println("Correct → non-admin blocked");
    }


    // TC012 update with existing email
    @Test(priority = 12)
    public void US25_TC012_existingEmail() {

        ObjectNode body = loadBase("user2");
        body.put("email", "Loma1@test.com"); // existing email

        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
        .body(body).put("/users/" + userID_2);

        System.out.println("Status Code: " + response.statusCode());

        Assert.assertEquals(response.statusCode(), 422);

        String msg = response.jsonPath().getString("errors.email[0]");
        System.out.println("Error Message → " + msg);
        System.out.println("System correctly rejected existing email.");
    }}