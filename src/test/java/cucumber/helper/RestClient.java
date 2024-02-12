package cucumber.helper;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
public class RestClient {

  public RequestSpecification getRequestSpecification() {
    int port = Integer.parseInt(System.getProperty("port"));

    return given()
        .port(port)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .urlEncodingEnabled(false)
        .when()
        .log()
        .everything();
  }
}
