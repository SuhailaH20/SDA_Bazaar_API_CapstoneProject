package tests;

import base_urls.apiBazaar;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigReader;

import static io.restassured.RestAssured.given;

public class US07_RemoveProductFromCartTests {

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
    public void TC001_removeExistingProduct() {
        Response response = given().spec(spec).delete("/cart/211");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.jsonPath().getBoolean("success"));
    }


    // ------------------ TC002 ------------------
    @Test
    public void TC002_removeLastItem_cartBecomesEmpty() {
        Response response = given().spec(spec).delete("/cart/49");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.jsonPath().getBoolean("success"));
    }

    // ------------------ TC003 ------------------
    @Test
    public void Bug_TC003_removeProductNotInCart() {
        Response response = given().spec(spec).delete("/cart/55");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 400);
        Assert.assertFalse(response.jsonPath().getBoolean("success"));
    }

    // ------------------ TC004 ------------------
    @Test
    public void TC004_invalidIDType() {
        Response response = given().spec(spec).delete("/cart/abc");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 400);
        Assert.assertFalse(response.jsonPath().getBoolean("success"));
    }

    // ------------------ TC005 ------------------
    @Test
    public void Bug_TC005_negativeID() {
        Response response = given().spec(spec).delete("/cart/-1");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 400);
        Assert.assertFalse(response.jsonPath().getBoolean("success"));
    }

    // ------------------ TC006 ------------------
    @Test
    public void Bug_TC006_missingIDInPath() {
        Response response = given().spec(spec).delete("/cart/");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 404);
    }

    // ------------------ TC007 ------------------
    @Test
    public void Bug_TC007_removeProductTwice() {
        // First removal (should succeed)
        given().spec(spec).delete("/cart/49");

        // Second removal (should fail)
        Response second = given().spec(spec).delete("/cart/49");
        second.prettyPrint();

        Assert.assertEquals(second.statusCode(), 400);
        Assert.assertFalse(second.jsonPath().getBoolean("success"));
    }

    // ------------------ TC008 ------------------
    @Test
    public void TC008_malformedRoute() {
        Response response = given().spec(spec).delete("/cart/1/abc");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 404);
    }

    // ------------------ TC009 ------------------
    @Test
    public void TC009_noToken() {
        Response response = given()
                .accept("application/json")
                .delete(ConfigReader.getApiBaseUrl() + "/cart/211");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401);
    }

    // ------------------ TC010 ------------------
    @Test
    public void TC010_tamperedToken() {
        Response response = given()
                .header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2JhemFhcnN0b3Jlcy5jb20vYXBpL2xvZ2luIiwiaWF0IjoxNzYzMzAyNjQ4LCJleHAiOjE3NjMzMDYyNDgsIm5iZiI6MTc2MzMwMjY0OCwianRpIjoiVnI4VndTTmZ4Q3lLV0ZoNCIsInN1YiI6IjM1MiIsInBydiI6IjIzYmQ1Yzg5NDlmNjAwYWRiMzllNzAxYzQwMDg3MmRiN2E1OTc2ZjcifQ.4MRS_LDzNNh2m5XkYiqtAopEbxDnDkrfA16QD0YofAk")
                .accept("application/json")
                .delete(ConfigReader.getApiBaseUrl() + "/cart/211");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401);
    }
}

