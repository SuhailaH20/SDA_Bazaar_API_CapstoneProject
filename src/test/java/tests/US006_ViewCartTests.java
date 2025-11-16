package tests;

import base_urls.apiBazaar;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigReader;

import static io.restassured.RestAssured.given;

public class US006_ViewCartTests {

    RequestSpecification spec;

    @BeforeClass
    public void setup() {
        spec = apiBazaar.spec(
                ConfigReader.getStoreManagerEmail(),
                ConfigReader.getDefaultPassword()
        );
    }

    // -------------------------- TC001 --------------------------
    @Test
    public void TC001_viewCartWithItems() {
        Response response = given()
                .spec(spec)
                .get("/cart");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.jsonPath().getBoolean("success"));
        Assert.assertTrue(response.jsonPath().getList("cart").size() > 0);
    }

    // -------------------------- TC002 --------------------------
    @Test
    public void TC002_viewEmptyCart() {
        Response response = given()
                .spec(spec)
                .get("/cart");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.jsonPath().getBoolean("success"));
        Assert.assertNotNull(response.jsonPath().getList("cart"));
    }

    // -------------------------- TC003 --------------------------
    @Test
    public void TC003_validatePriceCalculations() {
        Response response = given()
                .spec(spec)
                .get("/cart");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.jsonPath().getBoolean("success"));

        Assert.assertNotNull(response.jsonPath().get("totals"));
    }

    // -------------------------- TC004 --------------------------
    @Test
    public void TC004_validateItemStructure() {
        Response response = given()
                .spec(spec)
                .get("/cart");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);

        if (response.jsonPath().getList("cart").size() > 0) {
            Assert.assertTrue(response.jsonPath().getMap("cart[0]").containsKey("id"));
            Assert.assertTrue(response.jsonPath().getMap("cart[0]").containsKey("name"));
            Assert.assertTrue(response.jsonPath().getMap("cart[0]").containsKey("image"));
            Assert.assertTrue(response.jsonPath().getMap("cart[0]").containsKey("price"));
            Assert.assertTrue(response.jsonPath().getMap("cart[0]").containsKey("qty"));
        }
    }

    // -------------------------- TC005 --------------------------
    @Test
    public void TC005_invalidToken() {
        Response response = given()
                .header("Authorization", "Bearer invalid.token.value")
                .accept("application/json")
                .get(ConfigReader.getApiBaseUrl() + "/cart");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401);
    }

    // -------------------------- TC006 --------------------------
    @Test
    public void TC006_expiredToken() {
        Response response = given()
                .header("Authorization", "Bearer this.is.expired")
                .accept("application/json")
                .get(ConfigReader.getApiBaseUrl() + "/cart");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401);
    }

    // -------------------------- TC007 --------------------------
    @Test
    public void TC007_noToken() {
        Response response = given()
                .accept("application/json")
                .get(ConfigReader.getApiBaseUrl() + "/cart");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401);
    }

    // -------------------------- TC008 --------------------------
    @Test
    public void TC008_tamperedToken() {
        Response response = given()
                .header("Authorization", "Bearer tampered.token.value")
                .accept("application/json")
                .get(ConfigReader.getApiBaseUrl() + "/cart");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401);
    }

    // -------------------------- TC009 --------------------------
    @Test
    public void TC009_quantityEqualsOne() {
        Response response = given()
                .spec(spec)
                .get("/cart");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);

        if (response.jsonPath().getList("cart").size() > 0) {
            Assert.assertEquals(
                    response.jsonPath().getInt("cart[0].qty"),
                    1,
                    "Expected quantity to be 1 for edge case"
            );
        }
    }

    // -------------------------- TC010 --------------------------
    @Test
    public void TC010_largeQuantity() {
        Response response = given()
                .spec(spec)
                .get("/cart");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);

        if (response.jsonPath().getList("cart").size() > 0) {
            Assert.assertTrue(
                    response.jsonPath().getInt("cart[0].qty") > 100,
                    "Expected large quantity edge case (>100)"
            );
        }
    }
}
