package tests.stors;

import base_urls.apiBazaar;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import utilities.ConfigReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class US017_BrowseAllStores extends apiBazaar {//2Bug


 //[US17_TC001] [Positive]
    @Test
    public void testGetallStoresWithValidauthentication() {
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword())).get("/stores");
        response
                .then()
                .statusCode(200)
                .body("",hasSize(greaterThan(0)));


    }

//[US17_TC002][Positive]
    @Test
    public void testResponseContainsCompleteStoreInformation() {
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword())).get("/stores");
        response
                .then()
                .statusCode(200)
                .body("",hasSize(greaterThan(0)));

        int storesCount = response.jsonPath().getList("$").size();
        for (int i = 0; i < storesCount; i++) {
            response.then()
                    .body("[" + i + "].id", notNullValue())
                    .body("[" + i + "].name", notNullValue())
                    .body("[" + i + "].description", notNullValue())
                    .body("[" + i + "].location", notNullValue())
                    .body("[" + i + "].admin_id", notNullValue())
                    .body("[" + i + "].created_at", notNullValue())
                    .body("[" + i + "].updated_at", notNullValue());

        }
        System.out.println("Successfully verified " + storesCount + " stores");

    }

    //[US17_TC003][Positive]
    @Test
    public void testResponseTime() {
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword())).get("/stores");
        response
                .then()
                .statusCode(200)
                .time(lessThan(2000L));

    }
//[US17_TC004]#Bug
    @Test
    public void testRequestFailsWithInvalidBearertoken() {
        Response response = given(spec(ConfigReader.getCustomerEmail(),ConfigReader.getDefaultPassword())).get("/stores");
        response
                .then()
                .statusCode(401)
                .body("message", notNullValue(),"message", containsStringIgnoringCase("unauthenticated"));

    }


    //[US17_TC004]#Bug
    @Test
    public void testRequestFailsWithInvalidBearertoken2() {
        Response response = given(spec(ConfigReader.getStoreManagerEmail(),ConfigReader.getDefaultPassword())).get("/stores");
        response
                .then()
                .statusCode(401)
                .body("message", notNullValue(),"message", containsStringIgnoringCase("unauthenticated"));

    }

//[US17_TC005][Positive]
    @Test
    public void testResponseContentType() {

        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword())).get("/stores");
        response
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}
