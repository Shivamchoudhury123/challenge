package com.sourajyoti.model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "transactions")
public class Transaction {
	
	@Id
    @GeneratedValue (strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "reference")
	private String reference;
	//String generatedString = RandomStringUtils.randomAlphanumeric(10); USE apache commons
	
	@Column(name = "account_iban", nullable = false)
	@JsonProperty("account_iban")
	private String accountIban;
	
@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
private ZonedDateTime date;
	
	@Column(name = "amount",nullable = false)
	private Double amount;
	
	@Column(name = "fee")
	private double fee;
	
	@Column(name = "description")
	private String description;
	
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


//	public LocalDateTime getDate() {
//		return date;
//	}
//
//	public void setDate(LocalDateTime date) {
//		this.date = date;
//	}

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

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	


	
	

}
