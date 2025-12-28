package bank.infrastructure;

import bank.domain.exceptions.PersistenceException;
import bank.domain.model.Bank;
import bank.domain.service.BankRepository;


public class InMemoryBankRepository implements BankRepository {

    // Simule le "Fichier" ou la "Base de données"
    private Bank storedBank;

    @Override
    public void save(Bank bank) {

        this.storedBank = bank;
    }

    @Override
    public Bank load() throws PersistenceException {
        if (this.storedBank == null) {
            throw new PersistenceException("Aucune banque en mémoire (Repository vide).");
        }
        return this.storedBank;
    }
}