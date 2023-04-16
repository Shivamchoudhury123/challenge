package com.sourajyoti.model;

import javax.persistence.Entity;

@Entity
public class TransactionClient extends Transaction {
	public static final String CLIENT_STRING = "CLIENT";

	@Override
	public double getAmount() {
		return super.getAmount()-super.getFee();
	}
	@Override
	public String getChannel() {
		return CLIENT_STRING;
	}
	@Override
	public void setChannel(String channel) {
		super.setChannel(CLIENT_STRING);;
	}

}
