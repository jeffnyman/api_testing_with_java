import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BasicXmlTest {
    private Properties prop = new Properties();
    private String xmlPostPath;

    @Before
    public void getData() throws IOException {
        InputStream envProperties = this.getClass().getClassLoader().getResourceAsStream("env.properties");
        prop.load(envProperties);

        xmlPostPath = Objects.requireNonNull(getClass().getClassLoader().getResource("postdata.xml")).getPath();
    }

    @Test
    public void google_api_get_test() {
        RestAssured.baseURI = prop.getProperty("HOST");

        given().
                param("location", "-33.8670522,151.1957362").
                param("radius", "500").
                param("key", prop.getProperty("KEY")).

        when().
                get(Resources.placeGetDataXml()).

        then().assertThat().
                statusCode(200).and().
                contentType(ContentType.XML).and().
                body("PlaceSearchResponse.result[0].name", equalTo("Sydney")).and().
                body("PlaceSearchResponse.result[0].place_id", equalTo("ChIJP3Sa8ziYEmsRUKgyFmh9AQM")).and().
                header("Server", "scaffolding on HTTPServer2")
        ;
    }

    @Test
    public void google_api_post_test() throws IOException {
        RestAssured.baseURI = prop.getProperty("HOST");

        given().
                queryParam("key", prop.getProperty("KEY")).
                body(Payload.getPostDataXml(xmlPostPath)).

        when().
                post(Resources.placePostDataXml()).

        then().assertThat().
                statusCode(200).and().
                contentType(ContentType.XML).and().
                body("PlaceAddResponse.status", equalTo("OK"));
    }

    @Test
    public void google_api_delete_test() throws IOException {
        RestAssured.baseURI = prop.getProperty("HOST");

        Response response = given().
                queryParam("key", prop.getProperty("KEY")).
                body(Payload.getPostDataXml(xmlPostPath)).

        when().
                post(Resources.placePostDataXml()).

        then().assertThat().
                extract().response();

        XmlPath x = Utilities.rawToXml(response);
        String placeId = x.get("PlaceAddResponse.place_id");

        given().queryParam("key", prop.getProperty("KEY")).
                body("<PlaceDeleteRequest>" +
                        "<place_id>" + placeId + "</place_id>" +
                        "</PlaceDeleteRequest>").

        when().post(Resources.placeDeleteDataXml()).

        then().assertThat().
                statusCode(200).and().
                contentType(ContentType.XML).and().
                body("PlaceAddResponse.status", equalTo("OK"));
    }
}
