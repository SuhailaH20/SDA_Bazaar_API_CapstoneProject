package tests;

import base_urls.apiBazaar;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigReader;

import static io.restassured.RestAssured.given;

public class US08_ClearCartTests {

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
    public void Bug_TC001_clearCartWithItems() {
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
    public void Bug_TC002_clearEmptyCart() {
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
    public void TC003_clearWithoutToken() {
        Response response = given()
                .accept("application/json")
                .delete(ConfigReader.getApiBaseUrl() + "/cart/clear");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401);
    }

    // ------------------ TC004 ------------------
    @Test
    public void TC004_tamperedToken() {
        Response response = given()
                .header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2JhemFhcnN0b3Jlcy5jb20vYXBpL2xvZ2luIiwiaWF0IjoxNzYzMzAyNjQ4LCJleHAiOjE3NjMzMDYyNDgsIm5iZiI6MTc2MzMwMjY0OCwianRpIjoiVnI4VndTTmZ4Q3lLV0ZoNCIsInN1YiI6IjM1MiIsInBydiI6IjIzYmQ1Yzg5NDlmNjAwYWRiMzllNzAxYzQwMDg3MmRiN2E1OTc2ZjcifQ.4MRS_LDzNNh2m5XkYiqtAopEbxDnDkrfA16QD0YofAk")
                .accept("application/json")
                .delete(ConfigReader.getApiBaseUrl() + "/cart/clear");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401);
    }
}

