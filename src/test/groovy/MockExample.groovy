import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualto;
import static io.restassured.RestAssured.get;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClassPath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

class MockExample {
    private static WireMockServer wireMockServer;
    private static final String EVENTS_PATH = "/events?id=390"
    private static final String APPLICATION_JSON = "application/json"
    private static final String GAME_ODDS = getEventJson()

    @BeforeClass
    public static void before () throws Exception {
        System.out.println("Setting up!")
        final int port = UtiL.getAvailablePort()
        wireMockServer = new WireMockServer(port)
        wireMockServer.start();
        RestAssured.port = port
        configureFor ("localhost", port)
        stubFor (get(urlEqualto(EVENTS_PATH)).willReturn(
            aResponse().withStatus(200)
            .withHeader("Content-Type", APPLICATION_JSON)
            .withBody(GAME_ODDS)))
    }

    @Test
    public void givenUrl_whenCheckingFloatValuePasses_thenCorrect(){
        get("/events?id=390").then().assertThat()
            .body("odd.ck", equalTo (12.2f))
    }

    @Test
    public void givenUrl_whenSuccesOnGetsResponseAndJsonHasRequieredKV_thenCorrect(){
        get("/events?id=390").then().assertThat()
            .body("id", equalTo ("390"))
    }

    @Test
    public void givenUrl_whenJsonResponseHasArrayWithGivenValuesUnderKey_thenCorrect(){
        get("/events?id=390").then().assertThat()
            .body("odd.price", hasItems ("1.30", "5.25", "2.70", "1,20"))
    }

    @Test
    public void givenUrl_whenJsonResponseConformsToSchema_thenCorrect(){
        get("/events?id=390").then().assertThat()
            .body(matchesJsonSchemaInClassPath("evento.json"))
    }

    @Test
    public void whenWiremockIsStarted_thenReturningValidDataFromEndpoint() {
        JsonSchemaFactory jsonPath = JsonSchemaFactory
        .newBuilder()
        .setValidationConfiguration(
            ValidationConfiguration.newBuilder()
            .setDefaultVersion (SchemaVersion.DRAFTV4)
            .freeze ()).freeze();

        get ("/events?id=390")
        .then()
        .assertThat()
        .body(matchesJsonSchemaInClassPath("evento.json").using(
            JsonSchemaFactory));
    }

    @AfterClass
    public static void after() throws Exception{
        System.out.println("Running: tearDown");
        wireMockServer.stop();
    }

    private static String getEventJson(){
        return Util.inputStreaToString (RestAssuredIntegrationTest.class
        .getResourceAsStream ("/eventojson"));
    }
}
