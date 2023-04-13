package com.sourajyoti.repository;

import java.util.List;

import javax.persistence.Id;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sourajyoti.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Id> {

 Transaction findByReference(String reference);
 Transaction findByAccountIban(String accountIban);
 public List<Transaction> findByAccountIbanOrderByAmountAsc(String iban);
 public List<Transaction> findByAccountIbanOrderByAmountDesc(String iban);
Boolean existsByReference(String reference);

List<Transaction> findAllByAccountIban(String iban);

}
