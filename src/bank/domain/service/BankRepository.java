package bank.domain.service;

import bank.domain.exceptions.PersistenceException;
import bank.domain.model.Bank;

public interface BankRepository {
    void save(Bank bank) throws PersistenceException;
    Bank load() throws PersistenceException;
}