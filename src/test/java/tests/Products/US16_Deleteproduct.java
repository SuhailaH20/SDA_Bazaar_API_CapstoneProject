package tests.Products;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import utilities.ApiUtil;
import utilities.ConfigReader;
import utilities.ObjectMapperUtils;

public class US16_Deleteproduct {

    private String validToken;
    private String baseUrl;
    private String productId;

    @BeforeClass
    public void setup() {

        baseUrl = ConfigReader.getApiBaseUrl();

        validToken = ApiUtil.loginAndGetToken(
                ConfigReader.getStoreManagerEmail(),
                ConfigReader.getDefaultPassword()
        );

        ApiUtil.setToken(validToken);
    }

    // إنشاء منتج جديد قبل كل اختبار Delete لضمان وجود ID حقيقي
    @BeforeMethod
    public void createProductForDelete() {

        Response response = ApiUtil.post(
                baseUrl + "/products/create",
                ObjectMapperUtils.getJsonNode("createProduct").get("valid_product").toString()
        );

        productId = ApiUtil.getResponseValue(response, "product.id");

        System.out.println("Product ID for Delete Test = " + productId);
    }

    // TC001 - Negative - Delete without Token
    @Test
    public void deleteProductWithoutToken() {

        // إزالة التوكن
        ApiUtil.setToken(null);

        Response response = ApiUtil.delete(baseUrl + "/products/" + productId);
        ApiUtil.verifyStatusCode(response, 401);

        System.out.println("Delete Product WITHOUT Token Response:");
        System.out.println(response.asPrettyString());

        // إعادة التوكن للتستات القادمة
        ApiUtil.setToken(validToken);
    }

    // TC002 - Positive - Delete with Token
    @Test
    public void deleteProductWithToken() {

        Response response = ApiUtil.delete(baseUrl + "/products/" + productId);
        ApiUtil.verifyStatusCode(response, 200);

        System.out.println("Delete Product WITH Token Response:");
        System.out.println(response.asPrettyString());
    }

}
