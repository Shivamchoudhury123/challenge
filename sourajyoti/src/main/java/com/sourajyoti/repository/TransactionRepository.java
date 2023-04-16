package com.sourajyoti.repository;

import java.util.List;

import javax.persistence.Id;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sourajyoti.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Id> {

	public List<Transaction> findByReferenceOrderByDateDesc(String reference);

	public Transaction findByAccountIban(String accountIban);

	public List<Transaction> findByAccountIbanOrderByAmountAsc(String iban);

	public List<Transaction> findByAccountIbanOrderByAmountDesc(String iban);

	public List<Transaction> findAllByAccountIban(String iban);

}
