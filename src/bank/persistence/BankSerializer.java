package bank.persistence;

import bank.Bank;

public interface BankSerializer {
    String serialize(Bank bank);
    Bank deserialize(String data);
}