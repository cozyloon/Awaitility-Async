import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;

import java.util.concurrent.TimeUnit;

public class AsyncDemo {
    static Response response;
    static int statusCode;

    public static void main(String[] args) {

        RestAssured.given()
                .baseUri("https://apimocha.com/eventqueue")
                .basePath("/msg")
                .contentType(ContentType.JSON)
                .body("{\"id\":\"1\",\"name\":\"test\"}")
                .when().post();

        System.out.println("POST msg");

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(3, TimeUnit.SECONDS)
                .pollDelay(1, TimeUnit.SECONDS)
                .until(() -> {
                    response = RestAssured.get("https://apimocha.com/eventqueue/msg");
                    statusCode = response.getStatusCode();
                    return statusCode == HttpStatus.SC_CREATED;
                });

        String message = response.getBody().jsonPath().get("message").toString();
        System.out.println(message.equals("Msg Successfully Created") + " -> " + message);

    }
}
