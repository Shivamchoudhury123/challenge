package com.sourajyoti.model;

import javax.persistence.Entity;

@Entity
public class TransactionInternal extends Transaction {
	public static final String INTERNAL_STRING = "INTERNAL";

	@Override
	public String getChannel() {
		return INTERNAL_STRING;
	}
	@Override
	public void setChannel(String channel) {
		super.setChannel(INTERNAL_STRING);;
	}
}
