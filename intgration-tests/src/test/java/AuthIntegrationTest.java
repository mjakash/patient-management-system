import static org.hamcrest.CoreMatchers.notNullValue;
import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AuthIntegrationTest {

	@BeforeAll
	static void setUp() {
		RestAssured.baseURI = "http://localhost:4004";
	}

	@Test
	public void shouldReturnOKWithValidToken() {
		/*
		 * 1. Arrange 2. Act 3. Assert
		 */

		String loginPayload = """
				  {
				    "email": "testuser@test.com",
				    "password": "password123"
				  }
				""";

		Response response = given()
				.contentType("application/json")
				.body(loginPayload)
				.when()
				.post("/auth/login")
				.then()
				.statusCode(200)
				.body("token", notNullValue())
				.extract()
				.response();

		System.out.println("Generated Token: " + response.jsonPath().getString("token"));
	}
	
	@Test
	public void shouldReturnUnauthorizedWithInvalidToken() {
		/*
		 * 1. Arrange 2. Act 3. Assert
		 */

		String loginPayload = """
				  {
				    "email": "invalid_user@test.com",
				    "password": "invalidPass"
				  }
				""";

				given()
				.contentType("application/json")
				.body(loginPayload)
				.when()
				.post("/auth/login")
				.then()
				.statusCode(401);

	}
}
