package integration.flow;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.sourajyoti.StartChallengeApplication;
import com.sourajyoti.model.CreateTransactionDTO;
import com.sourajyoti.model.Transaction;
import com.sourajyoti.model.TransactionResponse;
import com.sourajyoti.repository.TransactionRepository;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

@SpringBootTest(classes = StartChallengeApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class HomeIntegrationTests {
	public final static String coreUrl = "http://localhost";
	private final String testIBANString = "ES9820385778983000760236";

	@LocalServerPort
	private int port;

	@Autowired
	TransactionRepository repository;

	@BeforeEach
	public void initUseCase() {
		RestAssured.baseURI = coreUrl + ":" + port;
	}

	private Response createTestTransaction(String reference, String iban, ZonedDateTime dt, double amount, double fee,
			String description, String channel) {

		CreateTransactionDTO transaction = new CreateTransactionDTO();
		transaction.setReference(reference);
		transaction.setAccountIban(iban);
		transaction.setDate(dt);
		transaction.setAmount(amount);
		transaction.setFee(fee);
		transaction.setDescription(description);

		Response response = given().body(transaction).contentType(ContentType.JSON).when()
				.post("/transaction/create/" + channel).then().statusCode(200).contentType("application/json").extract()
				.response();
		return response;
	}

	@Test
	public void test_SearchTransactionsByIbanAsc() {
		String payLoadString1 = " {\"reference\":\"12345A\"," + "\"account_iban\":\"" + testIBANString + "\","
				+ "\"date\":\"2019-07-16T16:55:42.000-0400\"," + "\"amount\":193.38," + "\"fee\":3.18,"
				+ "\"description\":\"Restaurant payment\"" + "}";

		given().body(payLoadString1).contentType(ContentType.JSON).when().post("/transaction/create/atm").then()
				.statusCode(200).contentType("application/json").extract().response();

		String payLoadString2 = " {\"reference\":\"12345A\"," + "\"account_iban\":\"" + testIBANString + "\","
				+ "\"date\":\"2019-07-16T16:55:42.000-0400\"," + "\"amount\":1000," + "\"fee\":3.18,"
				+ "\"description\":\"Restaurant payment\"" + "}";

		given().body(payLoadString2).contentType(ContentType.JSON).when().post("/transaction/create/atm").then()
				.statusCode(200).contentType("application/json").extract().response();

		Response result = given().contentType(ContentType.JSON).when()
				.get("/transaction/" + testIBANString + "/ascending").then().statusCode(200)
				.contentType("application/json").extract().response();

		List<Transaction> resultsList = Arrays.asList(result.getBody().as(Transaction[].class));

		assertNotNull(resultsList);

		double max = Double.MIN_VALUE;

		for (Transaction transaction : resultsList) {
			assertTrue(transaction.getAmount() > max);
			max = transaction.getAmount();

		}

	}

//
	@Test
	public void test_SearchTransactionsByIbanDesc() {

		String payLoadString1 = " {\"reference\":\"12345A\"," + "\"account_iban\":\"" + testIBANString + "\","
				+ "\"date\":\"2019-07-16T16:55:42.000-0400\"," + "\"amount\":193.38," + "\"fee\":3.18,"
				+ "\"description\":\"Restaurant payment\"" + "}";

		given().body(payLoadString1).contentType(ContentType.JSON).when().post("/transaction/create/atm").then()
				.statusCode(200).contentType("application/json").extract().response();

		String payLoadString = " {\"reference\":\"12345B\"," + "\"account_iban\":\"" + testIBANString + "\","
				+ "\"date\":\"2019-07-16T16:55:42.000-0400\"," + "\"amount\":100," + "\"fee\":3.18,"
				+ "\"description\":\"Restaurant payment\"" + "}";

		given().body(payLoadString).contentType(ContentType.JSON).when().post("transaction/create/atm").then()
				.statusCode(200).contentType("application/json").extract().response();

		Response response = given().contentType(ContentType.JSON).when()
				.get("/transaction/" + testIBANString + "/descending").then().statusCode(200)
				.contentType("application/json").extract().response();

		List<Transaction> resultsList = Arrays.asList(response.getBody().as(Transaction[].class));
		assertNotNull(resultsList);

		double min = Double.MAX_VALUE;

		for (Transaction transaction : resultsList) {
			assertTrue(transaction.getAmount() < min);
			min = transaction.getAmount();

		}
		assertEquals(200, response.getStatusCode());
		assertNotNull(resultsList); //

	}

	@Test
	public void validate_firstTransaction_deposit_positive() {

		String payLoadString = " {\"reference\":\"12345A\"," + "\"account_iban\":\"" + testIBANString + "\","
				+ "\"date\":\"2019-07-16T16:55:42.000-0400\"," + "\"amount\":10," + "\"fee\":3.18,"
				+ "\"description\":\"Restaurant payment\"" + "}";

		Response response1 = given().body(payLoadString).contentType(ContentType.JSON).when()
				.post("/transaction/create/atm").then().statusCode(200).contentType("application/json").extract()
				.response();

		assertEquals(200, response1.getStatusCode());

	}

	@Test
	public void validate_firstTransaction_deposit_negative() {

		String payLoadString = " {\"reference\":\"12345A\"," + "\"account_iban\":\"" + testIBANString + "\","
				+ "\"date\":\"2019-07-16T16:55:42.000-0400\"," + "\"amount\":-10," + "\"fee\":3.18,"
				+ "\"description\":\"Restaurant payment\"" + "}";

		Response response1 = given().body(payLoadString).contentType(ContentType.JSON).when()
				.post("/transaction/create/atm").then().statusCode(200).contentType("application/json").extract()
				.response();

		TransactionResponse responseTransaction1 = response1.getBody().as(TransactionResponse.class);
		assertEquals("insufficient funds", responseTransaction1.getStatus());
		assertEquals(200, response1.getStatusCode());

	}

	@Test
	public void validate_firstTransaction_deposit_amountLessThanFee() {

		String payLoadString = " {\"reference\":\"12345A\"," + "\"account_iban\":\"" + testIBANString + "\","
				+ "\"date\":\"2019-07-16T16:55:42.000-0400\"," + "\"amount\":2," + "\"fee\":3.18,"
				+ "\"description\":\"Restaurant payment\"" + "}";

		Response response1 = given().body(payLoadString).contentType(ContentType.JSON).when()
				.post("/transaction/create/atm").then().statusCode(200).contentType("application/json").extract()
				.response();
		TransactionResponse responseTransaction1 = response1.getBody().as(TransactionResponse.class);
		assertEquals("insufficient funds", responseTransaction1.getStatus());

	}

	@Test
	public void validate_transactions_sameIban_deposit_positive() {
		String payLoadString1 = " {\"reference\":\"12345A\"," + "\"account_iban\":\"" + testIBANString + "\","
				+ "\"date\":\"2019-07-16T16:55:42.000-0400\"," + "\"amount\":10," + "\"fee\":3.18,"
				+ "\"description\":\"1st payment\"" + "}";
		given().body(payLoadString1).contentType(ContentType.JSON).when().post("/transaction/create/atm").then()
				.statusCode(200).contentType("application/json").extract().response();
		String payLoadString2 = " {\"reference\":\"12345A\"," + "\"account_iban\":\"" + testIBANString + "\","
				+ "\"date\":\"2019-07-16T16:55:42.000-0400\"," + "\"amount\":-20," + "\"fee\":3.18,"
				+ "\"description\":\"2nd payment\"" + "}";
		Response response2 = given().body(payLoadString2).contentType(ContentType.JSON).when()
				.post("/transaction/create/atm").then().statusCode(200).contentType("application/json").extract()
				.response();
		String payLoadString3 = " {\"reference\":\"12345A\"," + "\"account_iban\":\"" + testIBANString + "\","
				+ "\"date\":\"2019-07-16T16:55:42.000-0400\"," + "\"amount\":40," + "\"fee\":3.18,"
				+ "\"description\":\"3rd payment\"" + "}";
		given().body(payLoadString3).contentType(ContentType.JSON).when().post("/transaction/create/atm").then()
				.statusCode(200).contentType("application/json").extract().response();
		String payLoadString4 = " {\"reference\":\"12345A\"," + "\"account_iban\":\"" + testIBANString + "\","
				+ "\"date\":\"2019-07-16T16:55:42.000-0400\"," + "\"amount\":-41.46," + "\"fee\":3.18,"
				+ "\"description\":\"4th payment\"" + "}";
		Response response4 = given().body(payLoadString4).contentType(ContentType.JSON).when()
				.post("/transaction/create/atm").then().statusCode(200).contentType("application/json").extract()
				.response();

		TransactionResponse responseTransaction2 = response2.getBody().as(TransactionResponse.class);
		TransactionResponse responseTransaction4 = response4.getBody().as(TransactionResponse.class);

		assertEquals("insufficient funds", responseTransaction2.getStatus());
		assertEquals("insufficient funds", responseTransaction4.getStatus());
	}

	@Test
	public void test_Status_invalid_status() {
		String payLoadString1 = " {\"reference\":\"12345A\"," + "\"account_iban\":\"" + testIBANString + "\","
				+ "\"date\":\"2019-07-16T16:55:42.000-0400\"," + "\"amount\":10," + "\"fee\":3.18,"
				+ "\"description\":\"1st payment\"" + "}";
		given().body(payLoadString1).contentType(ContentType.JSON).when().post("/transaction/create/atm").then()
				.statusCode(200).contentType("application/json").extract().response();
		String payLoadString = " {\"reference\":\"XXXXXX\"," + "\"channel\":\"ATM\"" + "}";

		Response response = given().body(payLoadString).contentType(ContentType.JSON).when().get("/transaction/status")
				.then().statusCode(200).contentType("application/json").extract().response();

		TransactionResponse responseTransaction = response.getBody().as(TransactionResponse.class);
		assertNotNull(responseTransaction);
		assertEquals("XXXXXX", responseTransaction.getReference());
		assertEquals("INVALID", responseTransaction.getStatus());
		assertEquals(200, response.getStatusCode());

	}

	@Test
	public void Client_beforeToday_settled() {
		createTestTransaction("12345A", testIBANString, ZonedDateTime.now().minusDays(1), 193.38, 3.18, "payment",
				"client");

		String payLoad1 = " {\"reference\":\"12345A\"," + "\"channel\":\"CLIENT\"" + "}";

		Response response2 = given().body(payLoad1).contentType(ContentType.JSON).when().get("/transaction/status")
				.then().statusCode(200).contentType("application/json").extract().response();

		TransactionResponse responseTransaction1 = response2.getBody().as(TransactionResponse.class);

		assertEquals("SETTLED", responseTransaction1.getStatus());

		assertEquals(190.2, responseTransaction1.getAmount());
	}

	@Test
	public void Atm_beforeToday_settled() {
		createTestTransaction("12345A", testIBANString, ZonedDateTime.now().minusDays(1), 193.38, 3.18, "payment",
				"atm");

		String payLoad2 = " {\"reference\":\"12345A\"," + "\"channel\":\"ATM\"" + "}";

		Response response2 = given().body(payLoad2).contentType(ContentType.JSON).when().get("/transaction/status")
				.then().statusCode(200).contentType("application/json").extract().response();

		TransactionResponse responseTransaction1 = response2.getBody().as(TransactionResponse.class);

		assertEquals("SETTLED", responseTransaction1.getStatus());

		assertEquals(190.2, responseTransaction1.getAmount());
	}//

	@Test
	public void Internal_beforeToday_settled() {
		createTestTransaction("12345A", testIBANString, ZonedDateTime.now().minusDays(1), 193.38, 3.18, "payment",
				"internal");

		String payLoadString1 = " {\"reference\":\"12345A\"," + "\"channel\":\"INTERNAL\"" + "}";

		Response response1 = given().body(payLoadString1).contentType(ContentType.JSON).when()
				.get("/transaction/status").then().statusCode(200).contentType("application/json").extract().response();

		TransactionResponse responseTransaction1 = response1.getBody().as(TransactionResponse.class);
		assertEquals("SETTLED", responseTransaction1.getStatus());
		assertEquals("12345A", responseTransaction1.getReference());
		assertEquals(193.38, responseTransaction1.getAmount());
		assertEquals(3.18, responseTransaction1.getFee());

	}

	@Test
	public void Client_Today_Pending() {
		createTestTransaction("12345A", testIBANString, ZonedDateTime.now().minusMinutes(10), 193.38, 3.18, "payment",
				"client");

		String payLoad1 = " {\"reference\":\"12345A\"," + "\"channel\":\"CLIENT\"" + "}";

		Response response2 = given().body(payLoad1).contentType(ContentType.JSON).when().get("/transaction/status")
				.then().statusCode(200).contentType("application/json").extract().response();

		TransactionResponse responseTransaction1 = response2.getBody().as(TransactionResponse.class);

		assertEquals("PENDING", responseTransaction1.getStatus());

		assertEquals(190.2, responseTransaction1.getAmount());
	}

	@Test
	public void Atm_Today_Pending() {
		createTestTransaction("12345A", testIBANString, ZonedDateTime.now().minusMinutes(10), 193.38, 3.18, "payment",
				"atm");

		String payLoad2 = " {\"reference\":\"12345A\"," + "\"channel\":\"ATM\"" + "}";

		Response response2 = given().body(payLoad2).contentType(ContentType.JSON).when().get("/transaction/status")
				.then().statusCode(200).contentType("application/json").extract().response();

		TransactionResponse responseTransaction1 = response2.getBody().as(TransactionResponse.class);

		assertEquals("PENDING", responseTransaction1.getStatus());

		assertEquals(190.2, responseTransaction1.getAmount());
	}//

	@Test
	public void Internal_Today_Pending() {
		createTestTransaction("12345A", testIBANString, ZonedDateTime.now().minusMinutes(10), 193.38, 3.18, "payment",
				"internal");

		String payLoadString1 = " {\"reference\":\"12345A\"," + "\"channel\":\"INTERNAL\"" + "}";

		Response response1 = given().body(payLoadString1).contentType(ContentType.JSON).when()
				.get("/transaction/status").then().statusCode(200).contentType("application/json").extract().response();

		TransactionResponse responseTransaction1 = response1.getBody().as(TransactionResponse.class);
		assertEquals("PENDING", responseTransaction1.getStatus());
		assertEquals("12345A", responseTransaction1.getReference());
		assertEquals(193.38, responseTransaction1.getAmount());
		assertEquals(3.18, responseTransaction1.getFee());

	}

	@Test
	public void Client_greaterDay_Future() {
		createTestTransaction("12345A", testIBANString, ZonedDateTime.now().plusDays(3), 193.38, 3.18, "payment",
				"client");
		String payLoadString1 = " {\"reference\":\"12345A\"," + "\"channel\":\"CLIENT\"" + "}";

		Response response1 = given().body(payLoadString1).contentType(ContentType.JSON).when()
				.get("/transaction/status").then().statusCode(200).contentType("application/json").extract().response();

		assertEquals(200, response1.getStatusCode());
		TransactionResponse responseTransaction1 = response1.getBody().as(TransactionResponse.class);
		assertEquals("FUTURE", responseTransaction1.getStatus());
		assertEquals("12345A", responseTransaction1.getReference());
		assertEquals(190.2, responseTransaction1.getAmount());

	}

	@Test
	public void ATM_greaterDay_Future() {
		createTestTransaction("12345A", testIBANString, ZonedDateTime.now().plusDays(3), 193.38, 3.18, "payment",
				"atm");
		String payLoadString1 = " {\"reference\":\"12345A\"," + "\"channel\":\"ATM\"" + "}";

		Response response1 = given().body(payLoadString1).contentType(ContentType.JSON).when()
				.get("/transaction/status").then().statusCode(200).contentType("application/json").extract().response();

		TransactionResponse responseTransaction1 = response1.getBody().as(TransactionResponse.class);
		assertEquals("FUTURE", responseTransaction1.getStatus());
		assertEquals("12345A", responseTransaction1.getReference());
		assertEquals(190.2, responseTransaction1.getAmount());

	}

	@Test
	public void Internal_greaterDay_Future() {
		createTestTransaction("12345A", testIBANString, ZonedDateTime.now().plusDays(3), 193.38, 3.18, "payment",
				"internal");
		String payLoadString1 = " {\"reference\":\"12345A\"," + "\"channel\":\"INTERNAL\"" + "}";

		Response response1 = given().body(payLoadString1).contentType(ContentType.JSON).when()
				.get("/transaction/status").then().statusCode(200).contentType("application/json").extract().response();

		TransactionResponse responseTransaction1 = response1.getBody().as(TransactionResponse.class);
		assertEquals("FUTURE", responseTransaction1.getStatus());
		assertEquals("12345A", responseTransaction1.getReference());
		assertEquals(193.38, responseTransaction1.getAmount());
		assertEquals(3.18, responseTransaction1.getFee());

	}

}
