package tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import utilities.ApiUtil;

public class US13_View_details {

    private String validToken;

    @BeforeClass
    public void setup() {
        // تسجيل الدخول وجلب التوكن
        validToken = ApiUtil.loginAndGetToken("storemanager@sda.com", "Password.12345");
        ApiUtil.setToken(validToken);
    }


    // TC001 - Positive - Get product by ID
    @Test
    public void getProductById() {
        int productId = 1958;
        String endpoint = "/products/" + productId;

        Response response = ApiUtil.get(endpoint);

        ApiUtil.verifyStatusCode(response, 200);

        System.out.println("Product " + productId + " Response:");
        System.out.println(response.asPrettyString());

        String idInResponse = ApiUtil.getResponseValue(response, "id");
        assert Integer.parseInt(idInResponse) == productId : "Product ID mismatch!";
    }

    // ----------------------------------------------------
    // TC002 - Positive - Get products by category
    // ----------------------------------------------------
    @Test
    public void getProductsByCategory() {
        String category = "electronics";
        String endpoint = "/products?category=" + category;

        Response response = ApiUtil.get(endpoint);

        ApiUtil.verifyStatusCode(response, 200);

        System.out.println("Products in category '" + category + "':");
        System.out.println(response.asPrettyString());
    }

    // ----------------------------------------------------
    // TC003 - Negative - Get product 399 without token
    // ----------------------------------------------------
    @Test
    public void getProductWithoutToken() {
        int productId = 399;
        String endpoint = "/products/" + productId;

        Response response = ApiUtil.getRequestSpec().get(endpoint);

        ApiUtil.verifyStatusCode(response, 401);

        System.out.println("Response without token for product " + productId + ":");
        System.out.println(response.asPrettyString());
    }

    // ----------------------------------------------------
    // TC004 - Negative - Get product that does not exist
    // ----------------------------------------------------
    @Test
    public void getNonExistingProduct() {
        int productId = 999999;
        String endpoint = "/products/" + productId;

        Response response = ApiUtil.get(endpoint);

        ApiUtil.verifyStatusCode(response, 404);

        System.out.println("Response for non-existing product " + productId + ":");
        System.out.println(response.asPrettyString());
    }
}
