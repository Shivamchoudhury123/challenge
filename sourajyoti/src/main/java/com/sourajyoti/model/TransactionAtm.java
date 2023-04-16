package com.sourajyoti.model;

import javax.persistence.Entity;

@Entity
public class TransactionAtm extends Transaction {
	public static final String ATM_STRING = "ATM";

	@Override
	public double getAmount() {
		return  super.getAmount()-super.getFee();
	}
	@Override
	public String getChannel() {
		return ATM_STRING;
	}
	@Override
	public void setChannel(String channel) {
		super.setChannel(ATM_STRING);
	}
}
