package com.sourajyoti.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class TransactionResponse {
	
	@JsonInclude(Include.NON_NULL)

	private String reference;
	@JsonInclude(Include.NON_NULL)

	private String status;

	@JsonInclude(Include.NON_NULL)
	private double amount;
	
	@JsonInclude(Include.NON_NULL)
	private double fee;
	
	@JsonInclude(Include.NON_NULL)
	private double totalBalance;
	
	@JsonInclude(Include.NON_NULL)
	private String account_iban;
	
	@JsonInclude(Include.NON_NULL)
	private ZonedDateTime date;
	
	@JsonInclude(Include.NON_NULL)
	private String channel;
	
	
	
	

	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public double getTotalBalance() {
		return totalBalance;
	}
	public void setTotalBalance(double totalBalance) {
		this.totalBalance = totalBalance;
	}

	public ZonedDateTime getDate() {
		return date;
	}
	public void setDate(ZonedDateTime date) {
		this.date = date;
	}
	public String getAccount_iban() {
		return account_iban;
	}
	public void setAccount_iban(String account_iban) {
		this.account_iban = account_iban;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
}
