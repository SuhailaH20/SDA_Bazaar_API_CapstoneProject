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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;

public class US09_ViewFavoritesTests {

    RequestSpecification spec;
    JsonNode data;
    ObjectMapper mapper;

    @BeforeClass
    public void setup() {

        spec = BaseUrlCustomer.spec(
                "customer@sda.com",
                "Password.12345"
        );

        data = ObjectMapperUtils.getJsonNode("US09_view_favorites");
        mapper = new ObjectMapper();
    }

    @Test
    public void TC09_01_viewFavoritesWithProducts() throws IOException {
        Response response = given().spec(spec).get("/favorites");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);

        JsonNode favorites = mapper.readTree(response.asString());
        Assert.assertTrue(favorites.size() > 0, "Favorites list should not be empty");
    }

    @Test
    public void TC09_02_viewFavoritesNoProducts() throws IOException {

        Response getFavorites = given().spec(spec).get("/favorites");
        List<Integer> ids = getFavorites.jsonPath().getList("id");

        for (Integer id : ids) {
            Response deleteResponse = given().spec(spec).delete("/favorites/" + id);
            Assert.assertEquals(deleteResponse.statusCode(), 200);
        }

        Response response = given().spec(spec).get("/favorites");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);

        JsonNode favoritesAfterDelete = mapper.readTree(response.asString());
        Assert.assertTrue(favoritesAfterDelete.isEmpty(), "Favorites list should be empty");
    }

    @Test
    public void TC09_03_verifyRequiredFields() throws IOException {
        Response response = given().spec(spec).get("/favorites");
        response.prettyPrint();

        JsonNode favorites = mapper.readTree(response.asString());
        for (JsonNode item : favorites) {
            Assert.assertNotNull(item.get("id"), "id is missing");
            Assert.assertNotNull(item.get("user_id"), "user_id is missing");
            Assert.assertNotNull(item.get("product_id"), "product_id is missing");
            Assert.assertNotNull(item.get("created_at"), "created_at is missing");
            Assert.assertNotNull(item.get("updated_at"), "updated_at is missing");
            Assert.assertNotNull(item.get("product"), "product object is missing");

            JsonNode product = item.get("product");
            Assert.assertNotNull(product.get("id"), "product.id is missing");
            Assert.assertNotNull(product.get("name"), "product.name is missing");
            Assert.assertNotNull(product.get("price"), "product.price is missing");
        }
    }

    @Test
    public void TC09_04_accessFavoritesWithoutToken() {
        Response response = given()
                .accept("application/json")
                .get(ConfigReader.getApiBaseUrl() + "/favorites");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401, "Expected unauthorized status when no token provided");
    }
}
