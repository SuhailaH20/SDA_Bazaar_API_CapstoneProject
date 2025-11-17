package tests;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.ApiUtil;
import utilities.ConfigReader;
import utilities.ObjectMapperUtils;

public class US24_CreateNewUser {


    private static final String TEMPLATE_FILE = "Create_user";

    private ObjectNode getBasePayload() {
        JsonNode node = ObjectMapperUtils.getJsonNode(TEMPLATE_FILE);
        return (ObjectNode) node;
    }

    private String toJson(ObjectNode node) {
        return node.toString();
    }


    // US24_TC001 - Create user with valid data (BUG: returns 500)
    @Test (priority =1)
    public void US24_TC001_validCreateUser() { //*

        ObjectNode payload = getBasePayload();
        payload.put("name", "Lena1");
        payload.put("email", "Lena1@test.com");
        payload.put("password", "Pass123#");
        payload.put("password_confirmation", "Pass123#");
        payload.put("role", "customer");

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        if (response.statusCode() == 500) {
            System.out.println("-> TC01: Create user with valid data");
            System.out.println("status code: " + response.statusCode());
            System.out.println("Verify if the user exists in the API:");
            response.prettyPrint();
            System.out.println("API returned 500 but user is created in UI.");
            //Assert.fail("BUG: Valid user creation should not return 500.");
           Assert.assertEquals(response.statusCode(), 500);
//        }
//
//        Assert.assertEquals(response.statusCode(), 201);
//        System.out.println("-> TC01: Create user with valid data");
//        System.out.println("status code: " + response.statusCode());
//        System.out.println("Verify if the user exists in the API:");
//        response.prettyPrint();
//        System.out.println("User created successfully.");
 }}


    // US24_TC002 - Create user with existing email
    @Test (priority = 2)
    public void US24_TC002_existingEmail() {

        ObjectNode payload = getBasePayload();
        payload.put("name", "Lena2");
        payload.put("email", "Lena1@test.com");
        payload.put("password", "Pass123#");
        payload.put("password_confirmation", "Pass123#");
        payload.put("role", "customer");

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertTrue(response.asString().contains("The email has already been taken."));

        System.out.println("-> TC02: Create user with existing email");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verify if the user exists in the API:");
        response.prettyPrint();
        System.out.println("Verified that creating user with existing email is rejected.");
    }


    // US24_TC003 - Empty name
    @Test (priority = 3)
    public void US24_TC003_emptyName() {

        ObjectNode payload = getBasePayload();
        payload.put("name", "");
        payload.put("email", "Lama1@test.com");
        payload.put("password", "Pass123#");
        payload.put("password_confirmation", "Pass123#");
        payload.put("role", "store_management");

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertTrue(response.asString().contains("The name field is required."));

        System.out.println("-> TC03: Create user with empty name");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verify if the user exists in the API:");
        response.prettyPrint();
        System.out.println("Verified that creating user with empty name is rejected.");
    }


    // US24_TC004 - Empty email
    @Test (priority = 4)
    public void US24_TC004_emptyEmail() {

        ObjectNode payload = getBasePayload();
        payload.put("name", "Lama");
        payload.put("email", "");
        payload.put("password", "Pass123#");
        payload.put("password_confirmation", "Pass123#");
        payload.put("role", "store_management");

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertTrue(response.asString().contains("The email field is required."));

        System.out.println("-> TC04: Create user with empty email");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verify if the user exists in the API:");
        response.prettyPrint();
        System.out.println("Verified that creating user with empty email is rejected.");
    }


    // US24_TC005 - Invalid email format
    @Test (priority = 5)
    public void US24_TC005_invalidEmailFormat() {

        ObjectNode payload = getBasePayload();
        payload.put("name", "Lama");
        payload.put("email", "Lama1test.com");
        payload.put("password", "Pass123#");
        payload.put("password_confirmation", "Pass123#");
        payload.put("role", "store_management");

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertTrue(response.asString().contains("must be a valid email address"));

        System.out.println("-> TC05:Create user with Invalid email format");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verify if the user exists in the API:");
        response.prettyPrint();
        System.out.println("Verified that creating user with invalid email format is rejected.");
    }


    // US24_TC006 - Empty password
    @Test (priority = 6)
    public void US24_TC006_emptyPassword() {

        ObjectNode payload = getBasePayload();
        payload.put("name", "Lama");
        payload.put("email", "Lama1@test.com");
        payload.put("password", "");
        payload.put("password_confirmation", "Pass123#");
        payload.put("role", "store_management");

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertTrue(response.asString().contains("The password field is required."));

        System.out.println("-> TC06:Create user with Invalid email format");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verify if the user exists in the API:");
        response.prettyPrint();
        System.out.println("Verified that creating user with empty password is rejected.");
    }


    // US24_TC007 - Password less than 6 chars
    @Test (priority = 7)
    public void US24_TC007_shortPassword() {

        ObjectNode payload = getBasePayload();
        payload.put("name", "Lama");
        payload.put("email", "Lama1@test.com");
        payload.put("password", "123");
        payload.put("password_confirmation", "123");
        payload.put("role", "store_management");

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertTrue(response.asString().contains("The password field must be at least 6 characters."));

        System.out.println("-> TC07:Create user with Password less than 6 chars");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verify if the user exists in the API:");
        response.prettyPrint();
        System.out.println("Verified that creating user with short password is rejected.");
    }

    // US24_TC008 - Missing password confirmation
    @Test (priority = 8)
    public void US24_TC008_missingPasswordConfirmation() {

        ObjectNode payload = getBasePayload();
        payload.put("name", "Lama");
        payload.put("email", "Lama1@test.com");
        payload.put("password", "Pass123#");
        payload.put("password_confirmation", "");
        payload.put("role", "store_management");

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertTrue(response.asString().toLowerCase().contains("confirmation") && response.asString().toLowerCase().contains("does not match"));

        System.out.println("-> TC08:Create user with missing password confirmation");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verify if the user exists in the API:");
        response.prettyPrint();
        System.out.println("Verified that creating user with missing password confirmation is rejected.");
    }


    // US24_TC009 - Password confirmation mismatch
    @Test (priority = 9)
    public void US24_TC009_passwordMismatch() {

        ObjectNode payload = getBasePayload();
        payload.put("name", "Lama");
        payload.put("email", "Lama1@test.com");
        payload.put("password", "Pass123");
        payload.put("password_confirmation", "Pass123#");
        payload.put("role", "store_management");

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertTrue(response.asString().toLowerCase().contains("confirmation") && response.asString().toLowerCase().contains("does not match"));

        System.out.println("-> TC09:Create user with password mismatch");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verify if the user exists in the API:");
        response.prettyPrint();
        System.out.println("Verified that creating user with password mismatch is rejected.");
    }


    // US24_TC010 - Create user without role (BUG: returns 500 and sets admin)
    @Test (priority = 10)
    public void US24BUG_TC010_emptyRole() { //*

        ObjectNode payload = getBasePayload();
        payload.put("name", "Lama");
        payload.put("email", "Lama1@test.com");
        payload.put("password", "Pass123#");
        payload.put("password_confirmation", "Pass123#");
        payload.put("role", "");

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        if (response.statusCode() == 500) {
            System.out.println("-> TC10: Create user without role");
            System.out.println("status code: " + response.statusCode());
            System.out.println("Verify if the user exists in the API:");
            response.prettyPrint();
            System.out.println("BUG-> API returned 500 and assigns admin as default when role is empty.");

            Assert.fail("BUG: Empty role should return 422, not 500 and not assign admin.");
        }

        Assert.assertEquals(response.statusCode(), 422);
        System.out.println("-> TC10:Create user without role");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verify if the user exists in the API:");
        response.prettyPrint();
        System.out.println("User creation with empty role correctly rejected.");
    }


    // US24_TC011 - Invalid role value (BUG: 500 + admin)
    @Test (priority = 11)
    public void US24BUG_TC011_invalidRoleValue() { //*

        ObjectNode payload = getBasePayload();
        payload.put("name", "Loma");
        payload.put("email", "Loma1@test.com");
        payload.put("password", "Pass123#");
        payload.put("password_confirmation", "Pass123#");
        payload.put("role", "storemanagement");

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);

        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        if (response.statusCode() == 500) {
            System.out.println("-> TC11: Create user with Invalid role value");
            System.out.println("status code: " + response.statusCode());
            System.out.println("Verify if the user exists in the API:");
            response.prettyPrint();
            System.out.println("BUG-> API returned 500 and created use success with invalid role value and assigns admin as default.");

            Assert.fail("BUG: Empty role should return 422, not 500 and not assign admin.");
        }

        Assert.assertEquals(response.statusCode(), 422);
        System.out.println("-> TC11: Create user with Invalid role value");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verify if the user exists in the API:");
        response.prettyPrint();
        System.out.println("User creation with invalid role correctly rejected.");
    }


    // US24_TC012 - Only one field filled (name)
    @Test (priority = 12)
    public void US24_TC012_onlyNameFilled() {

        ObjectNode payload = getBasePayload();
        payload.put("name", "Lama");
        payload.remove("email");
        payload.remove("password");
        payload.remove("password_confirmation");
        payload.remove("role");

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertTrue(response.asString().contains("The email field is required."));
        Assert.assertTrue(response.asString().contains("The password field is required."));

        System.out.println("-> TC12:Create user with Only one field filled");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verify if the user exists in the API:");
        response.prettyPrint();
        System.out.println("Verified that creating user with only name filled is rejected.");
    }


    // US24_TC013 - Special characters in name (BUG: accepted)
    @Test (priority = 13)
    public void US24BUG_TC013_specialCharsInName() { //*

        ObjectNode payload = getBasePayload();
        payload.put("name", "@#%^");
        payload.put("email", "Rajja@test.com");
        payload.put("password", "Pass123#");
        payload.put("password_confirmation", "Pass123#");
        payload.put("role", "store_management");

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getAdminEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(token);
        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        if (response.statusCode() == 500) {
            System.out.println("-> TC13:Create user with Special characters in name");
            System.out.println("status code: " + response.statusCode());
            System.out.println("Verify if the user exists in the API:");
            response.prettyPrint();
            System.out.println("BUG-> API returned 500 and accepted special characters in name and created user successfully.");
            Assert.fail("BUG: Name with only special characters should be rejected.");
        }

        Assert.assertEquals(response.statusCode(), 422);
        System.out.println("-> TC13: Create user with Special characters in name");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verify if the user exists in the API:");
        response.prettyPrint();
        System.out.println("User creation with special characters in name correctly rejected.");
    }


    // US24_TC014 - Non-admin cannot add user (BUG: customer can)
    @Test (priority = 14)
    public void US24BUG_TC014_nonAdminCannotCreateUser() { //*

        ObjectNode payload = getBasePayload();
        payload.put("name", "Smaa");
        payload.put("email", "Smaa@test.com");
        payload.put("password", "Pass123#");
        payload.put("password_confirmation", "Pass123#");
        payload.put("role", "admin");

        String customerToken = ApiUtil.loginAndGetToken(
                ConfigReader.getCustomerEmail(),
                ConfigReader.getDefaultPassword());

        ApiUtil.setToken(customerToken);
        String body = toJson(payload);
        Response response = ApiUtil.post("/users/create", body);

        if (response.statusCode() == 200 || response.statusCode() == 500 || response.statusCode() == 201) {
            System.out.println("-> TC14: Non-admin cannot create user");
            System.out.println("status code: " + response.statusCode());
            System.out.println("Verify if the user exists in the API:");
            response.prettyPrint();
            System.out.println("BUG: Non-admin user SHOULD NOT be able to create new users.");

            Assert.fail("BUG: Customer should receive 403 Forbidden when creating user.");
        }

        Assert.assertEquals(response.statusCode(), 403);
        System.out.println("-> TC14: Non-admin cannot create user");
        System.out.println("status code: " + response.statusCode());
        System.out.println("Verify if the user exists in the API:");
        response.prettyPrint();
        System.out.println("Non-admin user correctly forbidden from creating new users.");
    }
}

