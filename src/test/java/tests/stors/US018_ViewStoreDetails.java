package tests.stors;

import base_urls.apiBazaar;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class US018_ViewStoreDetails extends apiBazaar {
    public static int StoreID;
    @BeforeClass//run befor class to get first store id
    public void setup() {
        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
                .get("/stores");
        JsonPath jsonPath = response.jsonPath();
        StoreID = jsonPath.getInt("[0].id");
    }

    //[US18_TC001][Positive]
    @Test
    public void testRetrieveDetailedInformationforValidStoreID() {
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword())).get("/stores/"+StoreID);
        response
                .then()
                .statusCode(200)
                .body("id", equalTo(StoreID))
                .body("name", notNullValue())
                .body("description", notNullValue())
                .body("location", notNullValue())
                .body("admin_id", notNullValue())
                .body("created_at", notNullValue())
                .body("updated_at", notNullValue());
    }
//[US18_TC002][Negative]
    @Test
    public void testReturns404ErrorWhenStoreIDdoesNotExist() {

        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword())).get("/stores/77");//not exist
        response
                .then()
                .statusCode(404)
                .body("error",equalTo("Store not found"));
    }


    //[US18_TC003]
    @Test
    public void testWithInvalidStoreIDFormat() {

        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword())).get("/stores/@#");
        response
                .then()
                .statusCode(404)
                .body("error",equalTo("Store not found"));

    }
//[US18_TC004] #Bug
    @Test
    public void testRequestFailsWithInvalidBearertoken() {
        Response response = given(spec(ConfigReader.getCustomerEmail(),ConfigReader.getDefaultPassword())).get("/stores/"+StoreID);
        response
                .then()
                .statusCode(401)
                .body("message",equalTo("Unauthenticated."));

    }

    //[US18_TC004] #Bug
    @Test
    public void testRequestFailsWithInvalidBearertoken2() {
        Response response = given(spec(ConfigReader.getStoreManagerEmail(),ConfigReader.getDefaultPassword())).get("/stores/"+StoreID);
        response
                .then()
                .statusCode(401)
                .body("message",equalTo("Unauthenticated."));

    }

//[US18_TC005]
    @Test
    public void testFieldsinResponsehaveCorrectDatatypes() {
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword())).get("/stores/"+StoreID);
        response
                .then()
                .statusCode(200)
                .body("id",isA(Integer.class)
                        ,"name",instanceOf(String.class)
                        ,"location", instanceOf(String.class)
                        ,"description", instanceOf(String.class)
                        ,"admin_id", instanceOf(Integer.class)
                        ,"created_at", instanceOf(String.class)
                        ,"updated_at", instanceOf(String.class));

    }
}
