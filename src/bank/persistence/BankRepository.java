package bank.persistence;

import bank.Bank;

public interface BankRepository {
    void save(Bank bank) throws PersistenceException;
    Bank load() throws PersistenceException;
}