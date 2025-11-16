package tests;

import base_urls.apiBazaar;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utilities.ConfigReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class US021_DeleteStore extends apiBazaar {
    public static int StoreID;
    @BeforeMethod//run befor class to get first store id
    public void setup() {
        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
                .get("/stores");
        JsonPath jsonPath = response.jsonPath();
        StoreID = jsonPath.getInt("[0].id");
    }

//[US21_TC001]
    @Test
    public void testdeletestorewithvalidID() {
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .delete("/stores/"+StoreID);

        response
                .then()
                .statusCode(200)
                .body("success",equalTo("Store deleted successfully!"));
    }


//[US21_TC002]
@Test
public void teststoreisactuallydeletedfromdatabase() {
    Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
            .delete("/stores/"+StoreID);

    response
            .then()
            .statusCode(200)
            .body("success",equalTo("Store deleted successfully!"));

    Response response1=given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword())).get("/stores/"+StoreID);
     response1
             .then()
             .body("error",equalTo( "Store not found"));

}
//[US21_TC003]
@Test
public void testDeletestorewithnonexistentID() {
    Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
            .delete("/stores/77");

    response
            .then()
            .statusCode(500)
            .body("error",equalTo( "Store deletion failed. Please try again."));
}
//[US21_TC004] #Bug
    @Test
    public void testDeletestorewithinvalidToken() {
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .delete("/stores/"+StoreID);

        response
                .then()
                .statusCode(401)
                .body("message",equalTo( "Unauthenticated."));
    }


}
