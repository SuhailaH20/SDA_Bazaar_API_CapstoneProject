package tests.Products;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import utilities.ApiUtil;
import utilities.ConfigReader;
import utilities.ObjectMapperUtils;
import com.fasterxml.jackson.databind.JsonNode;

public class US14_Createproduct {

    private String validToken;
    private JsonNode data;
    private String baseUrl;

    // نخزّن ID المنتج هنا عشان نستخدمه في أي تست ثاني مثل
    public static String createdProductId;

    @BeforeClass
    public void setup() {
        baseUrl = ConfigReader.getApiBaseUrl();
        validToken = ApiUtil.loginAndGetToken(
                ConfigReader.getStoreManagerEmail(),
                ConfigReader.getDefaultPassword()
        );
        ApiUtil.setToken(validToken);
        data = ObjectMapperUtils.getJsonNode("createProduct");
    }

    // method to create product and verify response

    private void createProductAndVerify(JsonNode payload, int expectedStatusCode, String... errorFields) {

        Response response = ApiUtil.post(baseUrl + "/products/create", payload.toString());
        ApiUtil.verifyStatusCode(response, expectedStatusCode);

        System.out.println("Create Product Response:");
        System.out.println(response.asPrettyString());

        // في حالة النجاح (201) نتحقق ونخزّن الـ ID
        if (expectedStatusCode == 201) {

            String idInResponse = ApiUtil.getResponseValue(response, "product.id");
            assert idInResponse != null && !idInResponse.isEmpty() : "Product ID should not be null";

            // نخزّنه عشان نستخدمه لاحقاً في أي تست
            createdProductId = idInResponse;
            System.out.println("Product ID Saved: " + createdProductId);

            String nameInResponse = ApiUtil.getResponseValue(response, "product.name");
            assert nameInResponse.equals(payload.get("name").asText()) : "Product name mismatch";

        } else {
            // التحقق من رسائل الخطأ لكل حقل مطلوب
            for (String field : errorFields) {
                String message = ApiUtil.getResponseValue(response, "errors." + field + "[0]");
                assert message != null && !message.isEmpty() : field + " error message should not be null";
            }
        }
    }

    // TC001 - Positive
    @Test
    public void createValidProduct() {
        createProductAndVerify(data.get("valid_product"), 201);
    }

    // TC002 - Negative - Missing Name
    @Test
    public void createProductMissingName() {
        createProductAndVerify(data.get("missing_name_product"), 422, "name");
    }

    // TC003 - Negative - Invalid Data
    @Test
    public void createInvalidProduct() {
        createProductAndVerify(data.get("invalid_product"), 422, "price", "stock");
    }

    // TC004 - Negative - Without Token
    @Test
    public void createProductWithoutToken() {
        JsonNode payload = data.get("without_token_product");

        Response response = ApiUtil.getRequestSpec()
                .body(payload.toString())
                .post(baseUrl + "/products/create");

        ApiUtil.verifyStatusCode(response, 401);

        System.out.println("Create Product WITHOUT Token Response:");
        System.out.println(response.asPrettyString());
    }

    //TC005 - Negative - Duplicate SKU
    @Test
    public void createProductDuplicateSKU() {
        createProductAndVerify(data.get("duplicate_sku_product"), 422, "sku");
    }

    // TC006 - Negative - Missing Price
    @Test
    public void createProductMissingPrice() {
        createProductAndVerify(data.get("missing_price_product"), 422, "price");
    }

    // TC007 - Negative - Missing SKU
    @Test
    public void createProductMissingSKU() {
        createProductAndVerify(data.get("missing_sku_product"), 422, "sku");
    }

    // TC008 - Negative - Missing Stock
    @Test
    public void createProductMissingStock() {
        createProductAndVerify(data.get("missing_stock_product"), 422, "stock");
    }
}
