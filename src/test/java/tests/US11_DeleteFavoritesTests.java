package tests;

import base_urls.BaseUrlCustomer;
import base_urls.apiBazaar;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigReader;
import utilities.ObjectMapperUtils;

import static io.restassured.RestAssured.given;

public class US11_DeleteFavoritesTests {
    RequestSpecification spec;
    JsonNode data;

    @BeforeClass
    public void setup() {
        spec = BaseUrlCustomer.spec(
                "customer@sda.com",
                "Password.12345"
        );
        data = ObjectMapperUtils.getJsonNode("US11_delete_favorites");
    }

    // ------------------ TC11_01 ------------------
    @Test
    public void TC11_01_deleteExistingFavorite() {
        Response response = given()
                .spec(spec)
                .delete("/favorites/399");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("success"), "Favorite product deleted successfully!");
    }

    // ------------------ TC11_02 ------------------
    @Test
    public void TC11_02_deleteFavoriteNotInList() {
        Response response = given()
                .spec(spec)
                .delete("/favorites/99999");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 404);
        Assert.assertEquals(response.jsonPath().getString("error"), "Favorite not found.");
    }

    // ------------------ TC11_03 ------------------
    @Test
    public void TC11_03_deleteSameFavoriteTwice() {

        given().spec(spec).delete("/favorites/399");

        Response response = given().spec(spec).delete("/favorites/212");
        response.prettyPrint();
        Assert.assertEquals(response.statusCode(), 404);
        Assert.assertEquals(response.jsonPath().getString("error"), "Favorite not found.");
    }

    // ------------------ TC11_04 ------------------
    @Test
    public void TC11_04_deleteWithoutToken() {
        Response response = given()
                .accept("application/json")
                .delete(ConfigReader.getApiBaseUrl() + "/favorites/211");
        response.prettyPrint();

        Assert.assertEquals(response.statusCode(), 401);
    }
}
