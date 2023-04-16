package com.sourajyoti.service;

import java.util.List;

import javax.transaction.Transactional;

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

		if ((getTotalBalance(iban) + trial )< 0) {
			trResponse.setAmount(transaction.getAmount());
			trResponse.setReference(transaction.getReference());
			trResponse.setStatus("insufficient funds");
			return trResponse;

		}
		transactionRepository.save(transaction);
		trResponse.setAmount(transaction.getAmount());
		trResponse.setReference(transaction.getReference());
		trResponse.setStatus(transaction.getStatus());

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
		List<Transaction> transactionList = transactionRepository.findByReferenceOrderByDateDesc(reference);
		TransactionResponse response = new TransactionResponse();
		
		if (transactionList == null||transactionList.size()==0) {
			response.setReference(reference);
			response.setStatus("INVALID");
		} else {
			Transaction transaction = transactionList.get(0);
			response.setReference(transaction.getReference());
			response.setStatus(transaction.getStatus());
			response.setAmount(transaction.getAmount());
			response.setFee(transaction.getFee());

		}
		return response;
	}



}
