package base_urls;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import utilities.ConfigReader;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class BaseApi {

    private static String token;   // store once per session

    static {
        RestAssured.baseURI = ConfigReader.getApiBaseUrl();
    }

    // ---------------- Token Handling ----------------

    public static String getToken() {
        if (token == null) {
            token = loginAndGetToken(
                    ConfigReader.getAdminEmail(),
                    ConfigReader.getDefaultPassword()
            );
        }
        return token;
    }

    public static String loginAndGetToken(String email, String password) {
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        Response resp = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/login");

        return resp.jsonPath().getString("authorisation.token");
    }

    // --------------- Request Specification ----------------

    public static RequestSpecification spec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getApiBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + getToken())
                .build();
    }

    public static void setToken(String newToken) {
        token = newToken;
    }

    public static void clearToken() {
        token = null;
    }



}