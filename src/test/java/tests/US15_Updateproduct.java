package tests;
import base_urls.apiBazaar;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigReader;
import utilities.ObjectMapperUtils;

import static io.restassured.RestAssured.given;
public class US15_Updateproduct extends apiBazaar {

    private RequestSpecification spec;
    private JsonNode updateData;
    private JsonNode createData;

    @BeforeClass
    public void setup() {
        spec = apiBazaar.spec(
                ConfigReader.getStoreManagerEmail(),
                ConfigReader.getDefaultPassword()
        );
        updateData = ObjectMapperUtils.getJsonNode("updateProduct");
        createData = ObjectMapperUtils.getJsonNode("createProduct");
    }

    // ---------------- Helper: Create Product ----------------
    private String createTestProduct() {
        ObjectNode payload = (ObjectNode) createData.get("valid_product").deepCopy();
        payload.put("sku", "SKU-TEST-" + System.currentTimeMillis() + "-" + (int)(Math.random()*1000));

        Response response = given().spec(spec).body(payload.toString()).post("/products/create");
        response.prettyPrint();
        Assert.assertEquals(response.statusCode(), 201, "Could not create a product for the test.");
        return response.jsonPath().getString("product.id");
    }

    // ---------------- TC001 - Positive - Update Existing Product ----------------
    @Test(description = "TC001 - Positive - Update Existing Product")
    public void TC001_updateExistingProduct() {
        String productId = createTestProduct();
        JsonNode payload = updateData.get("valid_update");

        Response response = given().spec(spec).body(payload.toString()).put("/products/" + productId);
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("product.name"), payload.get("name").asText());
    }

    // ---------------- TC002 - Positive - Update Stock ----------------
    @Test(description = "TC002 - Positive - Update Stock")
    public void TC002_updateStock() {
        String productId = createTestProduct();
        ObjectNode payload = (ObjectNode) updateData.get("update_stock").deepCopy();
        payload.put("sku", "SKU-UPDATE-" + System.currentTimeMillis() + "-" + (int)(Math.random()*1000));

        Response response = given().spec(spec).body(payload.toString()).put("/products/" + productId);
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("product.stock"), payload.get("stock").asInt());
    }

    // ---------------- TC003 - Negative - Invalid Price ----------------
    @Test(description = "TC003 - Negative - Invalid Price")
    public void TC003_updateProduct_InvalidPrice() {
        String productId = createTestProduct();
        ObjectNode payload = (ObjectNode) updateData.get("invalid_price_update").deepCopy();
        payload.put("sku", "SKU-INVALID-" + System.currentTimeMillis() + "-" + (int)(Math.random()*1000));

        Response response = given().spec(spec).body(payload.toString()).put("/products/" + productId);
        response.prettyPrint();

        // فقط تحقق من 422 لأنه الخطأ المتوقع عادةً في API للتحقق من البيانات
        Assert.assertEquals(response.statusCode(), 422);
        String errorMessage = response.jsonPath().getString("errors.price[0]");
        Assert.assertNotNull(errorMessage);
    }

    // ---------------- TC004 - Negative - Update Without Token ----------------
    @Test(description = "TC004 - Negative - Update Without Token")
    public void TC004_updateWithoutToken() {
        String productId = createTestProduct();
        JsonNode payload = updateData.get("valid_update");

        Response response = given().body(payload.toString()).put("/products/" + productId);
        response.prettyPrint();

        // تحقق من التوكن
        Assert.assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }

    // ---------------- TC005 - Negative - Update Non-Existing Product ----------------
    @Test(description = "TC005 - Negative - Update Non-Existing Product")
    public void TC005_updateNonExistingProduct() {
        String nonExistingId = "999999999";
        ObjectNode payload = (ObjectNode) updateData.get("valid_update").deepCopy();
        payload.put("sku", "SKU-NONEXIST-" + System.currentTimeMillis() + "-" + (int)(Math.random()*1000));

        Response response = given().spec(spec).body(payload.toString()).put("/products/" + nonExistingId);
        response.prettyPrint();

        // تحقق فقط من الكود المتوقع للمنتج غير الموجود
        Assert.assertEquals(response.statusCode(), 404);

        String errorMessage = response.jsonPath().getString("error");
        if (errorMessage != null) {
            System.out.println("Error Message: " + errorMessage);
        }
    }
}