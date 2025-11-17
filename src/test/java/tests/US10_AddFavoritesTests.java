package tests;

import base_urls.BaseUrlCustomer;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigReader;
import utilities.ObjectMapperUtils;
import com.fasterxml.jackson.databind.JsonNode;

import static io.restassured.RestAssured.given;

public class US10_AddFavoritesTests {

    RequestSpecification spec;
    JsonNode data;

    @BeforeClass
    public void setup() {

        spec = BaseUrlCustomer.spec(
                "customer@sda.com",
                "Password.12345"
        );
        data = ObjectMapperUtils.getJsonNode("US10_add_favorites");
    }

    @Test
    public void TC10_01_addNewProduct() {
        JsonNode payload = data.get("valid_product");

        Response response = given()
                .spec(spec)
                .body(payload.toString())
                .post("/favorites/create");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("success"), "Product added favorites successfully!");
    }

    @Test
    public void TC10_02_addDuplicateProduct() {
        JsonNode payload = data.get("duplicate_product");

        // First addition
        given().spec(spec).body(payload.toString()).post("/favorites/create");

        // Second addition
        Response response = given()
                .spec(spec)
                .body(payload.toString())
                .post("/favorites/create");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 400);
        Assert.assertEquals(response.jsonPath().getString("error"), "Product is already in favorites.");
    }

    @Test
    public void BUG_TC10_03_missingProductId() { //Bug
        JsonNode payload = data.get("missing_product_id");

        Response response = given()
                .spec(spec)
                .body(payload.toString())
                .post("/favorites/create");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 400);
    }

    @Test
    public void BUG_TC10_04_nonExistingProduct() { //Bug
        JsonNode payload = data.get("non_existing_product");

        Response response = given()
                .spec(spec)
                .body(payload.toString())
                .post("/favorites/create");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 404);
    }
    @Test
    public void TC10_05_noToken() {
        JsonNode payload = data.get("valid_product");

        Response response = given()
                .contentType("application/json")
                .accept("application/json")
                .body(payload.toString())
                .post(ConfigReader.getApiBaseUrl() + "/favorites/create"); // استخدمنا الـ Base URL مباشرة
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401);
        Assert.assertEquals(response.jsonPath().getString("message"), "Unauthenticated.");
    }}
