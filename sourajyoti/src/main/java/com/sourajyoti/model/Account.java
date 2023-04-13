package com.sourajyoti.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "Accounts")
@DynamicUpdate
public class Account {
	
	@Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;
	
	//makeibanunique
	
	@Column(name = "account_iban" )
	private String iban;
	
	@Column(name = "current_balance")
      private double Balance;
	
	

	public Account() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}



	public double getBalance() {
		return Balance;
	}

	public void setBalance(double balance) {
		Balance = balance;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	

	

}
