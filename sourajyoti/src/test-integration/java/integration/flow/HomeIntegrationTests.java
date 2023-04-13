package integration.flow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.doubleThat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
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
import com.sourajyoti.model.Account;
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
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private final String testIBANString = "ES9820385778983000760236";
	private final String afterDay = "2019-07-17T16:55:42.000-0400";

	// ZonedDateTime zdtWithZoneOffset =
	// ZonedDateTime.parse("2019-07-17T16:55:42.000-0400", formatter);

	@LocalServerPort
	private int port;

	@Autowired
	TransactionRepository repository;

	@BeforeEach
	public void initUseCase() {
		RestAssured.baseURI = coreUrl + ":" + port;

		String payLoadString = " {\"reference\":\"12345A\"," + "\"account_iban\":\"" + testIBANString + "\","
				+ "\"date\":\"2019-07-16T16:55:42.000-0400\"," + "\"amount\":193.38," + "\"fee\":3.18,"
				+ "\"description\":\"Restaurant payment\"" + "}";

		Response response = given().body(payLoadString).contentType(ContentType.JSON).when().post("/transaction/create")
				.then().statusCode(200).contentType("application/json").extract().response();

	}

	@Test
	public void test_SearchTransactionsByIbanAsc() {
		Transaction transactionLarger = new Transaction();
		transactionLarger.setAccountIban(testIBANString);
		transactionLarger.setAmount(1000);

		Response response = given().body(transactionLarger).contentType(ContentType.JSON).when()
				.post("/transaction/create").then().statusCode(200).contentType("application/json").extract()
				.response();

		response = given().contentType(ContentType.JSON).when().get("/transaction/" + testIBANString + "/ascending")
				.then().statusCode(200).contentType("application/json").extract().response();

		List<Transaction> resultsList = Arrays.asList(response.getBody().as(Transaction[].class));

		System.out.println(resultsList.toString());

		double max = Double.MIN_VALUE;

		for (Transaction transaction : resultsList) {
			assertTrue(transaction.getAmount() > max);
			max = transaction.getAmount();

		}

	}
//
	@Test
	public void test_SearchTransactionsByIbanDesc() {
		Transaction transactionSmaller = new Transaction();
		transactionSmaller.setAccountIban(testIBANString);
		transactionSmaller.setAmount(100);

		Response response = given().body(transactionSmaller).contentType(ContentType.JSON).when()
				.post("/transaction/create").then().statusCode(200).contentType("application/json").extract()
				.response();

		response = given().contentType(ContentType.JSON).when().get("/transaction/" + testIBANString + "/descending")
				.then().statusCode(200).contentType("application/json").extract().response();

		// List<Transaction>resultsList = response.as(List.class);
		List<Transaction> resultsList = Arrays.asList(response.getBody().as(Transaction[].class));
		System.out.println(resultsList.toString());

		double min = Double.MAX_VALUE;

		for (Transaction transaction : resultsList) {
			assertTrue(transaction.getAmount() < min);
			min = transaction.getAmount();

		}

	}
//
	@Test
	public void test_Status_valid_status() {
		String payLoadString = " {\"reference\":\"12345A\"," + "\"channel\":\"CLIENT\"" + "}";

		Response response = given().body(payLoadString).contentType(ContentType.JSON).when().get("/transaction/status")
				.then().statusCode(200).contentType("application/json").extract().response();

		TransactionResponse responseTransaction = response.getBody().as(TransactionResponse.class);
		assertEquals("12345A", responseTransaction.getReference());
		assertEquals("valid", responseTransaction.getStatus());

	}
//
	@Test
	public void test_Status_invalid_status() {
		String payLoadString = " {\"reference\":\"XXXXXX\"," + "\"channel\":\"CLIENT\"" + "}";

		Response response = given().body(payLoadString).contentType(ContentType.JSON).when().get("/transaction/status")
				.then().statusCode(200).contentType("application/json").extract().response();

		TransactionResponse responseTransaction = response.getBody().as(TransactionResponse.class);
		assertEquals("XXXXXX", responseTransaction.getReference());
		assertEquals("invalid", responseTransaction.getStatus());

	}

//
//

	private Response createTestTransaction(String reference, String iban, ZonedDateTime dt, double amount, double fee,
			String description, String channel) {
		

		Transaction transaction = new Transaction();
		transaction.setReference(reference);
		transaction.setAccountIban(iban);
		transaction.setDate(dt);
		transaction.setAmount(amount);
		transaction.setFee(fee);
		transaction.setDescription(description);
		transaction.setChannel(channel);

		Response response = given().body(transaction).contentType(ContentType.JSON).when().post("/transaction/create")
				.then().statusCode(200).contentType("application/json").extract().response();
		return response;
	}
	
	
	private Response createTestTransactionForDateValidation(String reference, String iban, ZonedDateTime dt, double amount, double fee,
		 String channel) {
		

		TransactionResponse transaction = new TransactionResponse();
		transaction.setReference(reference);
		transaction.setAccount_iban(iban);
		transaction.setDate(dt);
		transaction.setAmount(amount);
		transaction.setFee(fee);
		transaction.setChannel(channel);

		Response response = given().body(transaction).contentType(ContentType.JSON).when().get("/transaction/status")
				.then().statusCode(200).contentType("application/json").extract().response();
		return response;
	}

	@Test
	public void validate_firstTransaction_deposit_positive() {


		Response response1 = createTestTransaction("12345A", "ES9820385778983000760237", ZonedDateTime.now(), 10, 3.18,
				"1st payment", "CLIENT");


		TransactionResponse responseTransaction1 = response1.getBody().as(TransactionResponse.class);

		assertEquals("ok", responseTransaction1.getStatus());


	}

	@Test
	public void validate_firstTransaction_deposit_negative() {

		Response response1 = createTestTransaction("12345A", "ES9820385778983000760237", ZonedDateTime.now(), -10, 3.18,
				"1st payment", "CLIENT");
		TransactionResponse responseTransaction1 = response1.getBody().as(TransactionResponse.class);
		assertEquals("insufficient funds", responseTransaction1.getStatus());
	}

	@Test
	public void validate_firstTransaction_deposit_amountLessThanFee() {

		Response response1 = createTestTransaction("12345A", "ES9820385778983000760237", ZonedDateTime.now(), 2, 3.18,
				"1st payment", "CLIENT");
		TransactionResponse responseTransaction1 = response1.getBody().as(TransactionResponse.class);
		assertEquals("insufficient funds", responseTransaction1.getStatus());
	}

	@Test
	public void validate_transactions_sameIban_deposit_positive() {

		Response response1 = createTestTransaction("12345A", "ES9820385778983000760237", ZonedDateTime.now(), 10, 3.18,
				"1st payment", "CLIENT");
		Response response2 = createTestTransaction("12345A", "ES9820385778983000760237", ZonedDateTime.now(), -20, 3.18,
				"2nd payment", "CLIENT");
		Response response3 = createTestTransaction("12345A", "ES9820385778983000760237", ZonedDateTime.now(), 40, 3.18,
				"3rd payment", "CLIENT");
		Response response4 = createTestTransaction("12345A", "ES9820385778983000760237", ZonedDateTime.now(), -41.46,
				3.18, "4th payment", "CLIENT");

		TransactionResponse responseTransaction1 = response1.getBody().as(TransactionResponse.class);
		TransactionResponse responseTransaction2 = response2.getBody().as(TransactionResponse.class);
		TransactionResponse responseTransaction3 = response3.getBody().as(TransactionResponse.class);
		TransactionResponse responseTransaction4 = response4.getBody().as(TransactionResponse.class);

		assertEquals("ok", responseTransaction1.getStatus());
		assertEquals("insufficient funds", responseTransaction2.getStatus());
		assertEquals("ok", responseTransaction3.getStatus());
		assertEquals("insufficient funds", responseTransaction4.getStatus());
	}
	
	
	@Test
	public void get_total_balance_by_iban() {

		createTestTransaction("12345A", "ES9820385778983000760237", ZonedDateTime.now(), 10, 3.18, "1st payment",
				"CLIENT");
		createTestTransaction("12345A", "ES9820385778983000760237", ZonedDateTime.now(), 10, 3.18, "2nd payment",
				"CLIENT");
		createTestTransaction("12345A", "ES9820385778983000760237", ZonedDateTime.now(), 10, 3.18, "3rd payment",
				"CLIENT");
	
		String findByIbanString =" {\"account_iban\":\"ES9820385778983000760237\"}";
		
		Response response = given().body(findByIbanString).contentType(ContentType.JSON).when().get("/transaction/getTotalBalance")
				.then().statusCode(200).contentType("application/json").extract().response();
		
		
		
		TransactionResponse responseTransaction1 = response.getBody().as(TransactionResponse.class);
		assertEquals(20.46, responseTransaction1.getTotalBalance());
	}

	@Test
	public void check_status_by_date() {
	Response response1 =	createTestTransactionForDateValidation("12345A", "ES9820385778983000760237", ZonedDateTime.now().minusDays(1), 10, 3.18, 
				"CLIENT");
	Response response2= 	createTestTransactionForDateValidation("12345A", "ES9820385778983000760237", ZonedDateTime.now().minusHours(1), 10, 3.18, 
				"CLIENT");
	TransactionResponse responseTransaction1 = response1.getBody().as(TransactionResponse.class);
	assertEquals("SETTLED", responseTransaction1.getStatus());
	TransactionResponse responseTransaction2 = response2.getBody().as(TransactionResponse.class);
	assertEquals("PENDING", responseTransaction2.getStatus());
	
	
	}
//	@Test
//	public void test_Status_settled_when_beforeToday_client() {
//		createTestTransaction("12345A", testIBANString, ZonedDateTime.now(), 50, 5, "misc", "CLIENT");
//
//		String payLoadString = " {\"reference\":\"12345A\"," + "\"channel\":\"CLIENT\"" + "}";
//
//		Response response = given().body(payLoadString).contentType(ContentType.JSON).when().get("/transaction/status")
//				.then().statusCode(200).contentType("application/json").extract().response();
//
//		TransactionResponse responseTransaction = response.getBody().as(TransactionResponse.class);
//		assertEquals("12345A", responseTransaction.getReference());
//		assertEquals(190.20, responseTransaction.getAmount());
//		assertEquals("SETTLED", responseTransaction.getStatus());
//
//	}
//	@Test
//	public void test_Status_settled_when_beforeToday_atm() {
//		String payLoadString = " {\"reference\":\"12345A\"," + "\"channel\":\"ATM\"" + "}";
//
//		Response response = given().body(payLoadString).contentType(ContentType.JSON).when().get("/transaction/status")
//				.then().statusCode(200).contentType("application/json").extract().response();
//
//		TransactionResponse responseTransaction = response.getBody().as(TransactionResponse.class);
//		assertEquals("XXXXXX", responseTransaction.getReference());
//		assertEquals("invalid", responseTransaction.getStatus());
//
//	}
}
