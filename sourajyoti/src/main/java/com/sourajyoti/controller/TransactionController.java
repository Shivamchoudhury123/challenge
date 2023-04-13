package com.sourajyoti.controller;

import java.awt.font.TransformAttribute;
import java.time.ZonedDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sourajyoti.model.Account;
import com.sourajyoti.model.Transaction;
import com.sourajyoti.model.TransactionResponse;
import com.sourajyoti.repository.AccountRepository;
import com.sourajyoti.repository.TransactionRepository;
import com.sourajyoti.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	TransactionRepository transactionRepository;
	@Autowired
	TransactionService transactionService;
	@Autowired
	AccountRepository accountRepository;

	@PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
	public TransactionResponse createTransaction(HttpServletRequest request, @RequestBody Transaction transaction) {
		return transactionService.doTransaction(transaction);
	}

	@GetMapping("/{iban}/{sort}")
	public List<Transaction> geTransactionsByIbanSorted(@PathVariable String iban, @PathVariable String sort) {
		List<Transaction> resultsList;

		resultsList = transactionService.findByIban(iban, sort);

		return resultsList;
	}

//	@GetMapping("/status")
//	public TransactionResponse getTransactionStatusByChannel(@RequestBody TransactionResponse payload) {
//		TransactionResponse response = transactionService.findByReference(payload.getReference());
//		return response;
//
//	}
	@GetMapping("/status")
	public TransactionResponse getTransactionStatusBydate(@RequestBody TransactionResponse payload) {
		TransactionResponse response = new TransactionResponse();
		String status= transactionService.checkDate(payload.getDate());
		response.setStatus(status);
		return response;

	}

	@GetMapping("/getTotalBalance")
	public TransactionResponse getTotalAccountBalance(@RequestBody TransactionResponse payload) {
		TransactionResponse response = new TransactionResponse();
		double totalBalance = transactionService.getTotalBalance(payload.getAccount_iban());
		response.setTotalBalance(totalBalance);
		return response;
	}

//	@GetMapping("status/{statusdate}/{reference}/{channel}")
//	public Transaction getTransactionStatusByChannel(@RequestParam("statusdate") @DateTimeFormat(iso = ISO.DATE_TIME)ZonedDateTime statusdate,@PathVariable String reference,@PathVariable String channel) {
//					//return  transactionService.findStatus(reference, channel);
//Transaction transaction = new Transaction();
//transaction.setDate(statusdate);
//transaction.setReference(reference);
//return transaction;
//
//	}

}
