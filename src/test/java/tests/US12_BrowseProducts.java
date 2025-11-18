package tests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import utilities.ApiUtil;

public class US12_BrowseProducts {

    @BeforeClass
    public void setup() {
        // Login و تخزين التوكن في اليوتلتز
        String token = ApiUtil.loginAndGetToken(
                "storemanager@sda.com",
                "Password.12345"
        );
        ApiUtil.setToken(token);
    }

    // TC001 - Positive - Get all products with valid token
    @Test
    public void getAllProducts() {
        Response response = ApiUtil.get("/products");
        ApiUtil.verifyStatusCode(response, 200);

        System.out.println("All Products Response:");
        System.out.println(response.asPrettyString());
    }

    // TC002 - Negative - Get product WITHOUT token
    @Test
    public void getProductWithoutToken() {
        int productId = 399;
        String endpoint = "/products/" + productId;

        // هنا نستخدم RequestSpec بدون إضافة توكن
        Response response = ApiUtil.getRequestSpec().get(endpoint);
        ApiUtil.verifyStatusCode(response, 401);

        System.out.println("Response WITHOUT token (product " + productId + "):");
        System.out.println(response.asPrettyString());
    }

    // TC003 - Negative - Get all products WITH invalid token
    @Test
    public void getProductsWithInvalidToken() {
        String invalidToken = "555g555g55g55g55g55g55g55g55g55g";

        Response response = ApiUtil.getWithAuth("/products", invalidToken);
        ApiUtil.verifyStatusCode(response, 401);

        System.out.println("Response WITH invalid token:");
        System.out.println(response.asPrettyString());
    }
}
