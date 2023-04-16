package com.sourajyoti.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sourajyoti.model.CreateTransactionDTO;
import com.sourajyoti.model.Transaction;
import com.sourajyoti.model.TransactionAtm;
import com.sourajyoti.model.TransactionClient;
import com.sourajyoti.model.TransactionInternal;
import com.sourajyoti.model.TransactionResponse;
import com.sourajyoti.repository.TransactionRepository;
import com.sourajyoti.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	TransactionRepository transactionRepository;
	@Autowired
	TransactionService transactionService;
	

	
	@PostMapping(value = "/create/atm", consumes = "application/json", produces = "application/json")
	public TransactionResponse createTransactionATM(HttpServletRequest request, @RequestBody CreateTransactionDTO createTransaction) {
		Transaction transaction = new TransactionAtm();
		setTransactionFields(transaction, createTransaction);
		transactionService.doTransaction(transaction);
		return transactionService.doTransaction(transaction);
	}
	
	@PostMapping(value = "/create/internal", consumes = "application/json", produces = "application/json")
	public TransactionResponse createTransactionInternal(HttpServletRequest request, @RequestBody CreateTransactionDTO createTransaction) {
		Transaction transaction = new TransactionInternal();
		setTransactionFields(transaction, createTransaction);
		transactionService.doTransaction(transaction);
		return transactionService.doTransaction(transaction);
	}
	
	@PostMapping(value = "/create/client", consumes = "application/json", produces = "application/json")
	public TransactionResponse createTransactionCllient(HttpServletRequest request, @RequestBody CreateTransactionDTO createTransaction) {
		Transaction transaction = new TransactionClient();
		setTransactionFields(transaction, createTransaction);
		transactionService.doTransaction(transaction);
		return transactionService.doTransaction(transaction);
	}
	
	private Transaction setTransactionFields(Transaction transaction, CreateTransactionDTO createTransaction) {
		transaction.setAccountIban(createTransaction.getAccountIban());
		transaction.setAmount(createTransaction.getAmount());
		transaction.setDate(createTransaction.getDate());
		transaction.setFee(createTransaction.getFee());
		transaction.setReference(createTransaction.getReference());
		return transaction;
	}

	@GetMapping("/{iban}/{sort}")
	public List<Transaction> geTransactionsByIbanSorted(@PathVariable String iban, @PathVariable String sort) {
		List<Transaction> resultsList;

		resultsList = transactionService.findByIban(iban, sort);

		return resultsList;
	}

	@GetMapping("/status")
	public TransactionResponse getTransactionStatusByChannel(@RequestBody Transaction payload) {
		TransactionResponse response = transactionService.findByReference(payload.getReference());
		return response;

	}


}
