package tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import utilities.ApiUtil;
import utilities.ConfigReader;
import utilities.ObjectMapperUtils;
import com.fasterxml.jackson.databind.JsonNode;

public class US15_Updateproduct {

    private String validToken;
    private JsonNode data;
    private String baseUrl;

    @BeforeClass
    public void setup() {
        baseUrl = ConfigReader.getApiBaseUrl();

        // تسجيل الدخول وجلب التوكن
        validToken = ApiUtil.loginAndGetToken(
                ConfigReader.getStoreManagerEmail(),
                ConfigReader.getDefaultPassword()
        );
        ApiUtil.setToken(validToken);

        // Load update test data
        data = ObjectMapperUtils.getJsonNode("updateProduct");

        System.out.println("Product ID from Create Test = " + US14_Createproduct.createdProductId);
    }

    private void updateProductAndVerify(String productId, JsonNode payload, int expectedStatusCode, String... errorFields) {

        // الاندبوينت الصحيح
        Response response = ApiUtil.put(
                baseUrl + "/products/" + productId,
                payload.toString()
        );

        ApiUtil.verifyStatusCode(response, expectedStatusCode);

        System.out.println("Update Product Response:");
        System.out.println(response.asPrettyString());

        if (expectedStatusCode == 200) {

            String idInResponse = ApiUtil.getResponseValue(response, "product.id");
            assert idInResponse != null && !idInResponse.isEmpty() : "Product ID should not be null";

            String nameInResponse = ApiUtil.getResponseValue(response, "product.name");
            assert nameInResponse.equals(payload.get("name").asText()) : "Product name mismatch";

        } else {
            for (String field : errorFields) {
                String message = ApiUtil.getResponseValue(response, "errors." + field + "[0]");
                assert message != null && !message.isEmpty() : field + " error message should not be null";
            }
        }
    }

    // TC001 - Positive - Update Existing Product
    @Test
    public void updateExistingProduct() {

        String productId = US14_Createproduct.createdProductId;
        updateProductAndVerify(productId, data.get("valid_update"), 200);
    }

    // TC002 - Positive - Update Stock
    @Test
    public void updateProductStock() {

        String productId = US14_Createproduct.createdProductId;
        updateProductAndVerify(productId, data.get("update_stock"), 200);
    }

    // TC003 - Negative - Invalid Price
    @Test
    public void updateProductInvalidPrice() {

        String productId = US14_Createproduct.createdProductId;
        updateProductAndVerify(productId, data.get("invalid_price_update"), 422, "price");
    }

    // TC004 - Negative - Update Without Token
    @Test
    public void updateProductWithoutToken() {

        String productId = US14_Createproduct.createdProductId;
        JsonNode payload = data.get("valid_update");

        Response response = ApiUtil.getRequestSpec()
                .body(payload.toString())
                .put(baseUrl + "/products/" + productId); // ← هنا برضه صححت الاندبوينت

        ApiUtil.verifyStatusCode(response, 401);

        System.out.println("Update Product WITHOUT Token Response:");
        System.out.println(response.asPrettyString());
    }

    // TC005 - Negative - Update Non-Existing Product
    @Test
    public void updateNonExistingProduct() {

        String nonExistingId = "999999";
        updateProductAndVerify(nonExistingId, data.get("valid_update"), 404);
    }
}
