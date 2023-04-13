package com.sourajyoti.repository;

import javax.persistence.Id;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sourajyoti.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Id> {

	Account findByIban(String iban);
	Boolean existsByIban(String iban);

}
