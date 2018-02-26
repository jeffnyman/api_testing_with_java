import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BasicTest {
    @Test
    public void google_api_get_test() {
        RestAssured.baseURI = "https://maps.googleapis.com";

        given().
                param("location", "-33.8670522,151.1957362").
                param("radius", "500").
                param("key", "AIzaSyBPWgf-UpHET6lIQK8NhKMuRwiHa_Ai0JM").

        when().
                get("/maps/api/place/nearbysearch/json").

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
        RestAssured.baseURI = "https://maps.googleapis.com";

        given().
                queryParam("key", "AIzaSyBPWgf-UpHET6lIQK8NhKMuRwiHa_Ai0JM").
                body("{" +
                        "\"location\": {" +
                        "\"lat\": -33.8669710," +
                        "\"lng\": 151.1958750" +
                        "}," +
                        "\"accuracy\": 50," +
                        "\"name\": \"Google Shoes!\"," +
                        "\"phone_number\": \"(02) 9374 4000\"," +
                        "\"address\": \"48 Pirrama Road, Pyrmont, NSW 2009, Australia\"," +
                        "\"types\": [\"shoe_store\"]," +
                        "\"website\": \"http://www.google.com.au/\"," +
                        "\"language\": \"en-AU\"" +
                        "}").
        when().
                post("/maps/api/place/add/json").

        then().assertThat().
                statusCode(200).and().
                contentType(ContentType.JSON).and().
                body("status", equalTo("OK"));

    }

    @Test
    public void google_api_delete_test() {
        RestAssured.baseURI = "https://maps.googleapis.com";

        Response response = given().
                queryParam("key", "AIzaSyBPWgf-UpHET6lIQK8NhKMuRwiHa_Ai0JM").
                body("{" +
                        "\"location\": {" +
                        "\"lat\": -33.8669710," +
                        "\"lng\": 151.1958750" +
                        "}," +
                        "\"accuracy\": 50," +
                        "\"name\": \"Google Shoes!\"," +
                        "\"phone_number\": \"(02) 9374 4000\"," +
                        "\"address\": \"48 Pirrama Road, Pyrmont, NSW 2009, Australia\"," +
                        "\"types\": [\"shoe_store\"]," +
                        "\"website\": \"http://www.google.com.au/\"," +
                        "\"language\": \"en-AU\"" +
                        "}").
        when().
                post("/maps/api/place/add/json").

        then().assertThat().
                extract().response();

        String rawResponse = response.asString();

        System.out.println(rawResponse);

        JsonPath jsonResponse = new JsonPath(rawResponse);

        String placeId = jsonResponse.get("place_id");

        System.out.println(placeId);

        given().queryParam("key", "AIzaSyBPWgf-UpHET6lIQK8NhKMuRwiHa_Ai0JM").
                body("{" +
                        "\"place_id\": \"" + placeId + "\"" +
                        "}").

        when().post("/maps/api/place/delete/json").

        then().assertThat().
                statusCode(200).and().
                contentType(ContentType.JSON).and().
                body("status", equalTo("OK"));

    }
}
