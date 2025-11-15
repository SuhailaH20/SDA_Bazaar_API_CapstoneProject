package base_urls;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utilities.ConfigReader;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class apiBazaar {

    public static RequestSpecification spec(String email, String password) {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getApiBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + getToken(email, password))
                .build();
    }

    private static String getToken(String email, String password) {
        Map payload = new HashMap();
        payload.put("email", email);
        payload.put("password", password);
        Response response = given()
                .body(payload)
                .contentType(ContentType.JSON)
                .post(ConfigReader.getApiBaseUrl() + "/login");
        return response.jsonPath().getString("authorisation.token");
    }

}
