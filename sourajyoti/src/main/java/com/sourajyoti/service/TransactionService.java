package com.sourajyoti.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.coyote.http11.filters.VoidInputFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sourajyoti.model.Transaction;
import com.sourajyoti.model.TransactionResponse;
import com.sourajyoti.repository.TransactionRepository;

@Service
public class TransactionService {
	@Autowired
	private TransactionRepository transactionRepository;

	@Transactional
	public TransactionResponse doTransaction(Transaction transaction) {
		String iban = transaction.getAccountIban();
		TransactionResponse trResponse = new TransactionResponse();
		double trial = transaction.getAmount() - transaction.getFee();

		System.out.println(getTotalBalance(iban) + trial);
		if (getTotalBalance(iban) + trial < 0) {
			trResponse.setAmount(transaction.getAmount());
			trResponse.setReference(transaction.getReference());
			trResponse.setStatus("insufficient funds");
			return trResponse;

		}
		transactionRepository.save(transaction);
		trResponse.setAmount(transaction.getAmount());
		trResponse.setReference(transaction.getReference());
		trResponse.setStatus("ok");
		return trResponse;
	}

	public double getTotalBalance(String iban) {
		List<Transaction> transactionList = transactionRepository.findAllByAccountIban(iban);
		double result = 0;
		double totalSum = 0;
		double totalFee = 0;

		if (transactionList != null) {

			for (Transaction tran : transactionList) {

				totalSum += tran.getAmount();
				totalFee += tran.getFee();

			}

			result = totalSum - totalFee;
			return result;

		}
		return result;

	}

	public List<Transaction> findByIban(String iban, String sort) {
		List<Transaction> reList;
		if (sort.equals("ascending")) {
			reList = transactionRepository.findByAccountIbanOrderByAmountAsc(iban);
		} else {
			reList = transactionRepository.findByAccountIbanOrderByAmountDesc(iban);
		}
		return reList;
	}

	public TransactionResponse findByReference(String reference) {
		TransactionResponse response = new TransactionResponse();
		Transaction transaction = transactionRepository.findByReference(reference);
		if (transaction == null) {
			response.setReference(reference);
			response.setStatus("invalid");
		} else {
			response.setReference(reference);
			response.setStatus("valid");
		}
		return response;
	}

	public String checkDate(ZonedDateTime creationDate) {
		String resultString;
		LocalDate transactionCreateDate = creationDate.toLocalDate();
		LocalDate yesterday = LocalDate.now().minusDays(1);
		if (transactionCreateDate.isBefore(yesterday)) {
			resultString = "SETTLED";

		} else {
			resultString = "PENDING";
		}
		return resultString;

	}
//	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
//	ZonedDateTime requestDate = ZonedDateTime.parse(date, formatter);
//	Date currentDate = Date.from(existingDate.toInstant());
//	Date sentDate = Date.from(requestDate.toInstant());
//
//	if (requestDate.toLocalDate().isAfter(existingDate.toLocalDate().plusDays(1))) {
//		String settledString = "settled";
//
//	}
//	if (currentDate.equals(sentDate)) {
//		String pendingString = "pending";
//	}

}
