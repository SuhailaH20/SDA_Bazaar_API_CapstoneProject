package utilities;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import base_urls.BaseApi;

public class ApiUtil {

    public static Response get(String endpoint) {
        return given()
                .spec(BaseApi.spec())
                .get(endpoint);
    }

    public static Response post(String endpoint, Object body) {
        return given()
                .spec(BaseApi.spec())
                .body(body)
                .post(endpoint);
    }

    public static Response put(String endpoint, Object body) {
        return given()
                .spec(BaseApi.spec())
                .body(body)
                .put(endpoint);
    }

    public static Response delete(String endpoint) {
        return given()
                .spec(BaseApi.spec())
                .delete(endpoint);
    }
}
