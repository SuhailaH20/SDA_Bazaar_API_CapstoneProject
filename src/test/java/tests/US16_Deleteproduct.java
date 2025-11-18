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

public class US16_Deleteproduct extends apiBazaar {

    private RequestSpecification spec;
    private JsonNode createData;
    private String productId1;
    private String productId2;

    @BeforeClass
    public void setup() {
        spec = apiBazaar.spec(
                ConfigReader.getStoreManagerEmail(),
                ConfigReader.getDefaultPassword()
        );

        createData = ObjectMapperUtils.getJsonNode("createProduct");
    }

    // ---------------- Helper: Create Product ----------------
    private String createTestProduct() {
        ObjectNode payload = (ObjectNode) createData.get("valid_product").deepCopy();
        // SKU فريد لكل تيست
        payload.put("sku", "SKU-TEST-" + System.currentTimeMillis() + "-" + (int)(Math.random()*1000));

        Response response = given().spec(spec).body(payload.toString()).post("/products/create");
        Assert.assertEquals(response.statusCode(), 201, "Could not create a product for the test.");
        return response.jsonPath().getString("product.id");
    }

    // ---------------- TC001 - Negative - Delete without Token ----------------
    @Test(description = "TC001 - Negative - Delete Without Token")
    public void deleteProductWithoutToken() {
        // إنشاء منتجين مؤقتين
        productId1 = createTestProduct();
        productId2 = createTestProduct();

        // حذف بدون توكن
        Response response1 = given().delete("/products/" + productId1);
        Response response2 = given().delete("/products/" + productId2);

        System.out.println("Delete Product WITHOUT Token Response 1:");
        response1.prettyPrint();
        System.out.println("Delete Product WITHOUT Token Response 2:");
        response2.prettyPrint();

        Assert.assertTrue(response1.statusCode() == 401 || response1.statusCode() == 403);
        Assert.assertTrue(response2.statusCode() == 401 || response2.statusCode() == 403);
    }

    // ---------------- TC002 - Positive - Delete with Token ----------------
    @Test(description = "TC002 - Positive - Delete With Token")
    public void deleteProductWithToken() {
        // إنشاء منتجين مؤقتين
        productId1 = createTestProduct();
        productId2 = createTestProduct();

        // حذف بالتوكن
        Response response1 = given().spec(spec).delete("/products/" + productId1);
        Response response2 = given().spec(spec).delete("/products/" + productId2);

        System.out.println("Delete Product WITH Token Response 1:");
        response1.prettyPrint();
        System.out.println("Delete Product WITH Token Response 2:");
        response2.prettyPrint();

        Assert.assertEquals(response1.statusCode(), 200);
        Assert.assertEquals(response2.statusCode(), 200);
    }
}
