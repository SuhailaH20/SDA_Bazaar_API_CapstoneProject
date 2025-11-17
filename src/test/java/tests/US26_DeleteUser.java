package tests;

import base_urls.apiBazaar;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigReader;

import static io.restassured.RestAssured.given;
import static utilities.ObjectMapperUtils.getJsonNode;

public class US26_DeleteUser extends apiBazaar  {

        public static int customerID;
        public static int storeManagerID;
        public static int adminID;
        public static int nonAdminID;

        // Load JSON file and return email
        private String getEmail(String key) {
            JsonNode node = getJsonNode("DeleteUser").get(key);
            return node.get("email").asText();
        }

        // BEFORE CLASS — Fetch IDs dynamically using emails
        @BeforeClass
        public void setup() {

            Response response = given(
                    spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
                    .get("/users");

            JsonPath json = response.jsonPath();

            customerID     = json.getInt("find { it.email == '" + getEmail("customer") + "' }.id");
            storeManagerID = json.getInt("find { it.email == '" + getEmail("store_manager") + "' }.id");
            adminID        = json.getInt("find { it.email == '" + getEmail("admin_user") + "' }.id");
            nonAdminID     = json.getInt("find { it.email == '" + getEmail("nonAdmin_User") + "' }.id");

            System.out.println("Fetched Customer ID → " + customerID);
            System.out.println("Fetched Store Manager ID → " + storeManagerID);
            System.out.println("Fetched Admin ID → " + adminID);
            System.out.println("Fetched nonAdmin ID → " + nonAdminID);
        }

        // TC001 Delete Customer (BUG: system creates all users as admin)
        @Test(priority = 1)
        public void US26BUG_TC001_deleteCustomer() {

            System.out.println("→ TC001: Delete Customer");


            if (customerID == 0) {
                System.out.println("BUG → Customer does NOT exist because API always sets role=admin.");
                Assert.fail("BUG: Customer not available for delete testing.");
            }


            Response response = given( spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
            .delete("/users/" + customerID);

            int status = response.statusCode();
            System.out.println("Status Code: " + status);

            if (status == 200) {
                System.out.println("BUG →  Customer deleted BUT system stored him as ADMIN so should NOT be deleted.");
                response.prettyPrint();
                Assert.fail("BUG: Customer incorrectly stored as admin AND deletion was allowed.");
            }


            Assert.assertEquals(status, 403, "Expected 403 Forbidden for deleting admin role.");
            System.out.println("Correct → System blocked deleting a user with admin role.");
        }


        // TC002 Delete Store Manager (BUG: Store Manager does not exist as role is always admin)

        @Test(priority = 2)
        public void US26BUG_TC002_deleteStoreManager() {

            System.out.println("→ TC002: Delete Store Manager");

            if (storeManagerID == 0) {
                System.out.println("BUG → Store Manager does NOT exist. API converts all roles to admin .");
                Assert.fail("BUG: Cannot delete Store Manager because his role is not created properly.");
            }

            Response response = given(
                    spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
                    .delete("/users/" + storeManagerID);

            int status = response.statusCode();
            System.out.println("Status Code: " + status);

            if (status == 200) {
                System.out.println("BUG → Store Manager deleted BUT API stored him as admin so should NOT be deleted.");
                response.prettyPrint();
                Assert.fail("BUG: Store Manager (treated as admin) should NOT be deleted.");
            }

            Assert.assertEquals(status, 403, "Expected 403 Forbidden for deleting admin role.");
            System.out.println("Correct → System blocked deleting an admin-role user.");
        }


        // TC003 Delete Admin should be forbidden
        @Test(priority = 3)
        public void US26BUG_TC003_deleteAdminSuccessfully() {

            System.out.println("→ TC003: Delete Admin");

            Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
            .delete("/users/" + adminID);

            int status = response.statusCode();
            System.out.println("Status Code: " + status);
            response.prettyPrint();

            if (status == 200) {
                System.out.println("BUG → REAL ADMIN WAS DELETED!");
                Assert.fail("BUG: Admin should NEVER be deleted.");
            }

            Assert.assertEquals(status, 403, "Expected 403 Forbidden when deleting admin.");
            System.out.println("Correct → System protected admin from deletion.");
        }


        // TC004 Attempt delete non-existing user
        @Test(priority = 4)
        public void US26_BUG_TC004_deleteNonExistingUser() {

            int nonExistingID = 3030;

            Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
            .delete("/users/" + nonExistingID);

            int status = response.statusCode();
            System.out.println("-> TC04: Delete non-existing user");
            System.out.println("Status Code: " + status);


            if (status == 500) {
                System.out.println("BUG → API returned 500 instead of 404");
                System.out.println("Response Body:");
                response.prettyPrint();
                Assert.fail("BUG: Expected 404 (User not found), but got 500 Internal Server Error");
            }

            Assert.assertEquals(status, 404);
            System.out.println("Correct → Non-existing user returned 404.");
        }


        // TC005 Delete without token → expect 401
        @Test(priority = 5)
        public void US26_BUG_TC005_nonAdminCannotDelete() {

            Response response = given( spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
            .delete("/users/" + nonAdminID);

            int status = response.statusCode();
            System.out.println("-> TC05: Non-admin delete attempt");
            System.out.println("Status Code: " + status);

            if (status == 200) {
                System.out.println("BUG → NON-ADMIN was able to DELETE the user!");
                System.out.println("Response:");
                response.prettyPrint();
                Assert.fail("BUG: Customer should NOT delete users. Expected 403 but got 200.");
            }

            Assert.assertEquals(status, 403);
            System.out.println("Correct → non-admin is restricted from deleting users.");
        }
    }
