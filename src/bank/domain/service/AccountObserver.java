package bank.domain.service;

import bank.domain.model.Account;
import bank.domain.model.Transaction;

public interface AccountObserver {
    void onTransaction(Account acc, Transaction tx);
}