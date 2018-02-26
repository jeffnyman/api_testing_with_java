import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BasicJsonTest {
    private Properties prop = new Properties();

    @Before
    public void getData() throws IOException {
        InputStream envProperties = this.getClass().getClassLoader().getResourceAsStream("env.properties");
        prop.load(envProperties);
    }

    @Test
    public void google_api_get_test() {
        RestAssured.baseURI = prop.getProperty("HOST");

        given().
                param("location", "-33.8670522,151.1957362").
                param("radius", "500").
                param("key", prop.getProperty("KEY")).

        when().
                get(Resources.placeGetDataJson()).

        then().assertThat().
                statusCode(200).and().
                contentType(ContentType.JSON).and().
                body("results[0].name", equalTo("Sydney")).and().
                body("results[0].place_id", equalTo("ChIJP3Sa8ziYEmsRUKgyFmh9AQM")).and().
                header("Server", "scaffolding on HTTPServer2")
        ;

    }

    @Test
    public void google_api_post_test() {
        RestAssured.baseURI = prop.getProperty("HOST");

        given().
                queryParam("key", prop.getProperty("KEY")).
                body(Payload.getPostDataJson()).
        when().
                post(Resources.placePostDataJson()).

        then().assertThat().
                statusCode(200).and().
                contentType(ContentType.JSON).and().
                body("status", equalTo("OK"));

    }

    @Test
    public void google_api_delete_test() {
        RestAssured.baseURI = prop.getProperty("HOST");

        Response response = given().
                queryParam("key", prop.getProperty("KEY")).
                body(Payload.getPostDataJson()).
        when().
                post(Resources.placePostDataJson()).

        then().assertThat().
                extract().response();

        JsonPath x = Utilities.rawToJson(response);
        String placeId = x.get("place_id");

        given().queryParam("key", prop.getProperty("KEY")).
                body("{" +
                        "\"place_id\": \"" + placeId + "\"" +
                        "}").

        when().post(Resources.placeDeleteDataJson()).

        then().assertThat().
                statusCode(200).and().
                contentType(ContentType.JSON).and().
                body("status", equalTo("OK"));

    }
}
