package tests;

import base_urls.apiBazaar;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static utilities.ObjectMapperUtils.*;

public class US20_UpdateExistingStore extends apiBazaar {//#2Bug
    public static int StoreID;
    @BeforeClass//run befor class to get first store id
    public void setup() {
        Response response = given(spec(ConfigReader.getAdminEmail(), ConfigReader.getDefaultPassword()))
                .get("/stores");
        JsonPath jsonPath = response.jsonPath();
        StoreID = jsonPath.getInt("[0].id");
    }

    //[US20_TC001]#Bug
    @Test
    public void testSuccessfulStoreUpdate() {
        //Prepare the expected data
        JsonNode payload = getJsonNode("UpdateStore");
        //send request
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).put("/stores/"+StoreID);
        response
                .then()
                .statusCode(200)
                .body("success",equalTo("Store update successfully!"),
                        "product.name",equalTo(payload.get("name").textValue()),
                        "product.description",equalTo(payload.get("description").textValue()),
                        "product.location",equalTo(payload.get("location").textValue()),
                        "product.admin_id",equalTo(payload.get("admin_id").intValue()));
    }
//[US20_TC002]#Bug
    @Test
    public void testUpdateStoreNotExist() {
        //Prepare the expected data
        JsonNode payload = getJsonNode("UpdateStore");
        //send request
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).put("/stores/77");
        response
                .then()
                .statusCode(404)
                .body("error",equalTo("Store not found"));



    }
//[US20_TC003]
    @Test
    public void testUpdatewithMissingRequiredFieldsName () {
        //Prepare the expected data
        JsonNode payload = getJsonNode("UpdateStore");
        removeFieldJsonNode(payload,"name");
        //send request
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).put("/stores/"+StoreID);
        response
                .then()
                .statusCode(422)
                .body("message",equalTo("The name field is required."),
                        "errors.name[0]",equalTo("The name field is required."));
    }

    //[US20_TC004]
    @Test
    public void testUpdatewithMissingRequiredFieldsLocation () {
        //Prepare the expected data
        JsonNode payload = getJsonNode("UpdateStore");
        removeFieldJsonNode(payload,"location");
        //send request
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).put("/stores/"+StoreID);
        response
                .then()
                .statusCode(422)
                .body("message",equalTo("The location field is required."),
                        "errors.location[0]",equalTo("The location field is required."));
    }

    //[US20_TC005]
    @Test
    public void testUpdatewithMissingRequiredFieldsDescription () {
        //Prepare the expected data
        JsonNode payload = getJsonNode("UpdateStore");
        removeFieldJsonNode(payload,"description");
        //send request
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).put("/stores/"+StoreID);
        response
                .then()
                .statusCode(422)
                .body("message",equalTo("The description field is required."),
                        "errors.description[0]",equalTo("The description field is required."));
    }

    //[US20_TC006]
    @Test
    public void testUpdatewithMissingRequiredFieldsAdmin () {
        //Prepare the expected data
        JsonNode payload = getJsonNode("UpdateStore");
        removeFieldJsonNode(payload,"admin_d");
        //send request
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).put("/stores/"+StoreID);
        response
                .then()
                .statusCode(422)
                .body("message",equalTo("The admin d field is required."),
                        "errors.admin_d[0]",equalTo("The admin d field is required."));
    }

    //[US20_TC007]
    @Test
    public void testUpdatewithNonexistingAdmin () {
        //Prepare the expected data
        JsonNode payload = getJsonNode("UpdateStore");
        updateJsonNode(payload,"admin_d",909);
        //send request
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).put("/stores/"+StoreID);
        response
                .then()
                .statusCode(500)
                .body("error",equalTo("Store update failed. Please try again."));
    }

}
