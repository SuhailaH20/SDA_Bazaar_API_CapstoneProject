package tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ApiUtil;
import utilities.ConfigReader;
import utilities.ObjectMapperUtils;

public class US14_Createproduct {

    private JsonNode data;

    @BeforeClass
    public void setup() {

        String token = ApiUtil.loginAndGetToken(
                ConfigReader.getStoreManagerEmail(),
                ConfigReader.getDefaultPassword()
        );

        Assert.assertNotNull(token, "Login Failed! Token is null.");

        data = ObjectMapperUtils.getJsonNode("createProduct");
    }

    //POSITIVE TEST

    @Test(priority = 1) // نعطيه أولوية ليعمل قبل اختبار الـ SKU المكرر
    public void TC001_createValidProduct() {
        // 1. إعداد البيانات (Payload)
        JsonNode originalPayload = data.get("valid_product");

        //  تعديل الـ SKU لضمان أنه فريد
        ObjectNode payload = (ObjectNode) originalPayload.deepCopy();
        payload.put("sku", "SKU-" + System.currentTimeMillis());

        // 2. إرسال الطلب باستخدام دالة post
        Response response = ApiUtil.post("/products/create", payload.toString());
        response.prettyPrint();

        //  التحقق من النتائج
        Assert.assertEquals(response.statusCode(), 201);
        Assert.assertEquals(response.jsonPath().getString("product.name"), payload.get("name").asText());
        Assert.assertNotNull(response.jsonPath().getString("product.id"), "Product ID should not be null");
    }

    // NEGATIVE TESTS

    @Test
    public void TC002_createProduct_MissingName() {
        JsonNode payload = data.get("missing_name_product");
        Response response = ApiUtil.post("/products/create", payload.toString());
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertNotNull(response.jsonPath().getString("errors.name[0]"));
    }

    @Test
    public void TC003_createProduct_InvalidDataTypes() {
        JsonNode payload = data.get("invalid_product");
        Response response = ApiUtil.post("/products/create", payload.toString());
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertNotNull(response.jsonPath().getString("errors.price[0]"));
        Assert.assertNotNull(response.jsonPath().getString("errors.stock[0]"));
    }

    @Test
    public void TC004_createProduct_WithoutToken() {
        JsonNode payload = data.get("valid_product");

        // نمسح التوكن مؤقتاً من ApiUtil قبل إرسال الطلب
        ApiUtil.clearToken();

        Response response = ApiUtil.post("/products/create", payload.toString());
        response.prettyPrint();

        // نعيد تسجيل الدخول لاستعادة التوكن لبقية الاختبارات
        ApiUtil.loginAndGetToken(
                ConfigReader.getStoreManagerEmail(),
                ConfigReader.getDefaultPassword()
        );

        Assert.assertEquals(response.statusCode(), 401);
        Assert.assertEquals(response.jsonPath().getString("message"), "Unauthenticated.");
    }

    @Test(priority = 2) // يعتمد على وجود منتج تم إنشاؤه في TC001
    public void TC005_createProduct_DuplicateSKU() {
        // نستخدم نفس بيانات المنتج الأصلي الذي يحتوي على SKU قد تم استخدامه
        JsonNode payload = data.get("valid_product");

        Response response = ApiUtil.post("/products/create", payload.toString());
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertNotNull(response.jsonPath().getString("errors.sku[0]"));
    }

    @Test
    public void TC006_createProduct_MissingPrice() {
        JsonNode payload = data.get("missing_price_product");
        Response response = ApiUtil.post("/products/create", payload.toString());
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertNotNull(response.jsonPath().getString("errors.price[0]"));
    }

    @Test
    public void TC007_createProduct_MissingSKU() {
        JsonNode payload = data.get("missing_sku_product");
        Response response = ApiUtil.post("/products/create", payload.toString());
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertNotNull(response.jsonPath().getString("errors.sku[0]"));
    }

    @Test
    public void TC008_createProduct_MissingStock() {
        JsonNode payload = data.get("missing_stock_product");
        Response response = ApiUtil.post("/products/create", payload.toString());
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 422);
        Assert.assertNotNull(response.jsonPath().getString("errors.stock[0]"));
    }
}
