package com.sourajyoti.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "channel")
@JsonSubTypes({ @JsonSubTypes.Type(value = TransactionAtm.class, name = "ATM"),
		@JsonSubTypes.Type(value = TransactionClient.class, name = "CLIENT"),
		@JsonSubTypes.Type(value = TransactionAtm.class, name = "INTERNAL") })
@JsonIgnoreProperties(ignoreUnknown = true)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "transactions")
public abstract class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "reference")
	private String reference;
	// String generatedString = RandomStringUtils.randomAlphanumeric(10); USE apache
	// commons

	@Column(name = "account_iban", nullable = false)
	@JsonProperty("account_iban")
	private String accountIban;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime date;

	@Column(name = "amount", nullable = false)
	private Double amount;

	@Column(name = "fee")
	private Double fee;

	@Column(name = "description")
	private String description;

	@JsonProperty("channel")
	@Column(name = "channel")
	private String channel; 

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAccountIban() {
		return accountIban;
	}

	public void setAccountIban(String accountIban) {
		this.accountIban = accountIban;
	}

	public ZonedDateTime getDate() {
		return date;
	}

	public void setDate(ZonedDateTime date) {
		this.date = date;
	}

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public  String getStatus() {
		String resultString;
		LocalDate transactionCreateDate = getDate().toLocalDate();
		LocalDate today = LocalDate.now();
		if (transactionCreateDate.isBefore(today)) {
			resultString = "SETTLED";

		} else if(transactionCreateDate.isAfter(today)) {
			resultString = "FUTURE";
		}
		else {
			resultString = "PENDING";
		}
		return resultString;

	}

			

}
