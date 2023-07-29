import io.restassured.RestAssured
import io.restassured.builder.ReequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.specificaction.RequestSpecification
import org.testng.annotations.BeforeClass

class Base {
    @BeforeClass
    public static void void setUpRestAssured() {
        RestAssured.baseURI = "http://qa-library-dev.herokuapp.com"
        RestAssured.basePath = "/api/";
        RequestSpecification requestSpecification = new ReequestSpecBuilder().
                addHeader("Content-Type", ContentType.JSON.toString()).
                addHeader("Accept", ContentType.JSON.toString())
                .build();
        RestAssured.requestSpecification = requestSpecification;
    }
}