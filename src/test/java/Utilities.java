import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;

public class Utilities {
    public static XmlPath rawToXml(Response response) {
        String rawResponse = response.asString();
        return new XmlPath(rawResponse);
    }

    public static JsonPath rawToJson(Response response) {
        String rawResponse = response.asString();
        return new JsonPath(rawResponse);
    }
}
