package tests.stors;

import base_urls.apiBazaar;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import utilities.ConfigReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static utilities.ObjectMapperUtils.*;

public class US019_CreateNewStore extends apiBazaar {//1#BUG

//[US19_TC001]
    @Test
    public void testStoreCreationwithAllRequiredFields() {
        //Prepare the expected data
        JsonNode payload = getJsonNode("AddStore");
        //send request
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).post("/stores/create");
        response
                .then()
                .statusCode(201)
                .body("success",equalTo("Store created successfully!"),
                        "product.name",equalTo(payload.get("name").textValue()),
                        "product.description",equalTo(payload.get("description").textValue()),
                        "product.location",equalTo(payload.get("location").textValue()),
                        "product.admin_id",equalTo(payload.get("admin_id").intValue()));

    }
//[US19_TC002] [Negative] Store Creation with Missing Required Field (Name)
    @Test
    public void testCreationwithMissingRequiredFieldName() {
        //Prepare the expected data
        JsonNode payload = getJsonNode("AddStore");
        removeFieldJsonNode(payload,"name");
        //send request
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).post("/stores/create");
        response
                .then()
                .statusCode(422)
                .body("message",equalTo("The name field is required."),
                        "errors.name[0]",equalTo("The name field is required."));

    }

    //[US19_TC003] [Negative] Store Creation with Missing Required Field (Admin)
    @Test
    public void testCreationwithMissingRequiredFieldAdmin() {
        //Prepare the expected data
        JsonNode payload = getJsonNode("AddStore");
        removeFieldJsonNode(payload,"admin_id");
        //send request
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).post("/stores/create");
        response
                .then()
                .statusCode(422)
                .body("message",equalTo("The admin id field is required."),
                        "errors.admin_id[0]",equalTo("The admin id field is required."));

    }


    //[US19_TC004] [Negative] Store Creation with Missing Required Field (Location)
    @Test
    public void testCreationwithMissingRequiredFieldLocation() {
        //Prepare the expected data
        JsonNode payload = getJsonNode("AddStore");
        removeFieldJsonNode(payload,"location");
        //send request
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).post("/stores/create");
        response
                .then()
                .statusCode(422)
                .body("message",equalTo("The location field is required."),
                        "errors.location[0]",equalTo("The location field is required."));

    }


    //[US19_TC005] [Negative] Store Creation with Missing Required Field (description)
    @Test
    public void testCreationwithMissingRequiredFielddescription() {
        //Prepare the expected data
        JsonNode payload = getJsonNode("AddStore");
        removeFieldJsonNode(payload,"description");
        //send request
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).post("/stores/create");
        response
                .then()
                .statusCode(422)
                .body("message",equalTo("The description field is required."),
                        "errors.description[0]",equalTo("The description field is required."));

    }


    //[US19_TC006] [Negative] Store Creation with Null Values
    @Test
    public void testCreationwithNullValues() {
        //Prepare the expected data
        JsonNode payload = getJsonNode("AddStore");
        updateJsonNode(payload,"name",null);
        updateJsonNode(payload,"description",null);
        updateJsonNode(payload,"location",null);
        updateJsonNode(payload,"admin_id",null);
        //send request
        Response response = given(spec(ConfigReader.getAdminEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).post("/stores/create");
        response
                .then()
                .statusCode(422)
                .body("message",equalTo("The name field is required. (and 3 more errors)"),
                        "errors.name[0]",equalTo("The name field is required."),
                        "errors.description[0]",equalTo("The description field is required."),
                        "errors.location[0]",equalTo("The location field is required."),
                        "errors.admin_id[0]",equalTo("The admin id field is required.")
                        );

    }
//[US19_TC007] #Bug
    @Test
    public void testCreationWithinvalidAuthentication() {
        //Prepare the expected data
        JsonNode payload = getJsonNode("AddStore");
        //send request
        Response response = given(spec(ConfigReader.getCustomerEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).post("/stores/create");
        response
                .then()
                .statusCode(401)
                .body("message",equalTo("Unauthenticated."));


    }

    //[US19TC008] [Negative] Adding Store with Admin Not exist in the Database
    @Test
    public void testStorewithAdminNotexistintheDatabase() {
        //Prepare the expected data
        JsonNode payload = getJsonNode("AddStore");
        updateJsonNode(payload,"admin_id",909);
        //send request
        Response response = given(spec(ConfigReader.getCustomerEmail(),ConfigReader.getDefaultPassword()))
                .body(payload).post("/stores/create");
        response
                .then()
                .statusCode(500)
                .body("error",equalTo("Store creation failed. Please try again."));


    }








}

