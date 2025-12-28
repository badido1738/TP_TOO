package bank.application;

import bank.domain.model.Bank;
import bank.domain.model.Account;
import bank.domain.service.BankRepository;
import bank.domain.service.AccountObserver;
import bank.domain.exceptions.PersistenceException;
import java.util.HashMap;

public class BankService {
    private Bank bank;
    private final BankRepository repository;

    public BankService(BankRepository repository) {
        this.repository = repository;
        try {
            this.bank = repository.load();
        } catch (PersistenceException e) {
            System.out.println("Création d'une nouvelle banque.");
            this.bank = new Bank(new HashMap<>());
        }
    }

    public void transfer(String fromId, String toId, double amount) {
        bank.transfer(fromId, toId, amount);
        save(); 
    }

    public void save() {
        repository.save(this.bank);
    }
    
    public Bank getBank() { return bank; }
    
    // pour ajouter l'audit
    public void addObserver(String accountId, AccountObserver observer) {
        Account acc = bank.getAccounts().get(accountId);
        if(acc != null) acc.addObserver(observer);
    }
}