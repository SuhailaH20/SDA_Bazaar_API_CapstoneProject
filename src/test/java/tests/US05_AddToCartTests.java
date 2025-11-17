package tests;

import base_urls.apiBazaar;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigReader;
import utilities.ObjectMapperUtils;
import com.fasterxml.jackson.databind.JsonNode;

import static io.restassured.RestAssured.given;

public class US05_AddToCartTests {

    RequestSpecification spec;
    JsonNode data;

    @BeforeClass
    public void setup() {
        spec = apiBazaar.spec(
                ConfigReader.getCustomerEmail(),
                ConfigReader.getDefaultPassword()
        );

        data = ObjectMapperUtils.getJsonNode("add_to_cart");
    }
    /// //

    // ---------------- POSITIVE ----------------

    @Test
    public void TC001_addProduct_defaultQuantity() {
        JsonNode payload = data.get("valid_product");

        Response response = given()
                .spec(spec)
                .body(payload.toString())
                .post("/cart/add");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.jsonPath().getBoolean("success"));
        Assert.assertEquals(response.jsonPath().getString("message"),
                "Product added to cart successfully");
        Assert.assertEquals(response.jsonPath().getInt("cart.product_id"),
                payload.get("product_id").asInt());
    }

    @Test
    public void TC002_addProduct_customQuantity() {
        JsonNode payload = data.get("valid_product_qty");

        Response response = given()
                .spec(spec)
                .body(payload.toString())
                .post("/cart/add");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.jsonPath().getBoolean("success"));
        Assert.assertEquals(response.jsonPath().getString("message"),
                "Product added to cart successfully");
        Assert.assertEquals(response.jsonPath().getInt("cart.product_id"),
                payload.get("product_id").asInt());
    }

    // ---------------- NEGATIVE ----------------

    @Test
    public void TC005_missingProductId() {
        JsonNode payload = data.get("missing_id");

        Response response = given()
                .spec(spec)
                .body(payload.toString())
                .post("/cart/add");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 400);
        Assert.assertFalse(response.jsonPath().getBoolean("success"));
        Assert.assertEquals(response.jsonPath().getString("message"),
                "Failed to add product to cart");
    }

    @Test
    public void TC006_invalidIDType() {
        JsonNode payload = data.get("invalid_id_type");

        Response response = given()
                .spec(spec)
                .body(payload.toString())
                .post("/cart/add");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 400);
        Assert.assertFalse(response.jsonPath().getBoolean("success"));
        Assert.assertEquals(response.jsonPath().getString("message"),
                "Failed to add product to cart");
    }

    @Test
    public void TC007_nonExistingProduct() {
        JsonNode payload = data.get("non_existing_id");

        Response response = given()
                .spec(spec)
                .body(payload.toString())
                .post("/cart/add");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 400);
        Assert.assertFalse(response.jsonPath().getBoolean("success"));
        Assert.assertEquals(response.jsonPath().getString("message"),
                "Failed to add product to cart");
    }

    @Test
    public void Bug_TC008_quantityZero() {
        JsonNode payload = data.get("qty_zero");

        Response response = given()
                .spec(spec)
                .body(payload.toString())
                .post("/cart/add");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 400);
        Assert.assertFalse(response.jsonPath().getBoolean("success"));
        Assert.assertEquals(response.jsonPath().getString("message"),
                "Failed to add product to cart");
    }

    @Test
    public void Bug_TC009_quantityNegative() {
        JsonNode payload = data.get("qty_negative");

        Response response = given()
                .spec(spec)
                .body(payload.toString())
                .post("/cart/add");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 400);
        Assert.assertFalse(response.jsonPath().getBoolean("success"));
        Assert.assertEquals(response.jsonPath().getString("message"),
                "Failed to add product to cart");
    }

    @Test
    public void Bug_TC010_quantityString() {
        JsonNode payload = data.get("qty_string");

        Response response = given()
                .spec(spec)
                .body(payload.toString())
                .post("/cart/add");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 400);
    }

    @Test
    public void Bug_TC011_quantityNull() {
        JsonNode payload = data.get("qty_null");

        Response response = given()
                .spec(spec)
                .body(payload.toString())
                .post("/cart/add");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 400);
        Assert.assertFalse(response.jsonPath().getBoolean("success"));
        Assert.assertEquals(response.jsonPath().getString("message"),
                "Failed to add product to cart");
    }

    @Test
    public void TC013_noToken() {
        JsonNode payload = data.get("valid_product");

        Response response = given()
                .contentType("application/json")
                .accept("application/json")
                .body(payload.toString())
                .post(ConfigReader.getApiBaseUrl() + "/cart/add");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401);
        Assert.assertEquals(response.jsonPath().getString("message"),
                "Unauthenticated.");
    }
}
