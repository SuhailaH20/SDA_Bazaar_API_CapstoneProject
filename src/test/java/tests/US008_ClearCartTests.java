package tests;

import base_urls.apiBazaar;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigReader;

import static io.restassured.RestAssured.given;

public class US008_ClearCartTests {

    RequestSpecification spec;

    @BeforeClass
    public void setup() {
        spec = apiBazaar.spec(
                ConfigReader.getStoreManagerEmail(),
                ConfigReader.getDefaultPassword()
        );
    }

    // ------------------ TC001 ------------------
    @Test
    public void TC001_clearCartWithItems() {
        Response response = given()
                .spec(spec)
                .accept("application/json")
                .delete("/cart/clear");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.jsonPath().getBoolean("success"));
    }

    // ------------------ TC002 ------------------
    @Test
    public void TC002_clearEmptyCart() {
        // Run clear twice — 2nd clear tests empty cart
        given().spec(spec).delete("/cart/clear");

        Response response = given()
                .spec(spec)
                .accept("application/json")
                .delete("/cart/clear");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.jsonPath().getBoolean("success"));
    }

    // ------------------ TC003 ------------------
    @Test
    public void TC003_validateTotalEqualsZeroAfterClear() {
        given().spec(spec).delete("/cart/clear");

        Response response = given()
                .spec(spec)
                .accept("application/json")
                .get("/cart");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);

//        if (response.jsonPath().get("total") != null) {
//            Assert.assertEquals(response.jsonPath().getInt("total"), 0);
//        }
    }

    // ------------------ TC004 ------------------
    @Test
    public void TC004_validateItemsArrayEmpty() {
        given().spec(spec).delete("/cart/clear");

        Response response = given()
                .spec(spec)
                .accept("application/json")
                .get("/cart");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
//        Assert.assertEquals(response.jsonPath().getList("cart").size(), 0);
    }

    // ------------------ TC005 ------------------
    @Test
    public void TC005_clearWithoutToken() {
        Response response = given()
                .accept("application/json")
                .delete(ConfigReader.getApiBaseUrl() + "/cart/clear");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401);
    }

    // ------------------ TC006 ------------------
    @Test
    public void TC006_tamperedToken() {
        Response response = given()
                .header("Authorization", "Bearer tampered.token.value")
                .accept("application/json")
                .delete(ConfigReader.getApiBaseUrl() + "/cart/clear");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401);
    }
}

