package bank.infrastructure;

import bank.domain.model.Bank;

public interface BankSerializer {
    String serialize(Bank bank);
    Bank deserialize(String data);
}