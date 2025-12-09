package bank;

import bank.tx.Transaction;

public interface AccountObserver {
    void onTransaction(Account acc, Transaction tx);
}