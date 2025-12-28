package bank.infrastructure;

import bank.domain.exceptions.PersistenceException;
import bank.domain.model.Bank;
import bank.domain.service.BankRepository;

/**
 * Implémentation "Bouchon" (Stub/Mock) du repository.
 * Stocke les données en mémoire vive.
 * Les données sont perdues à l'arrêt du programme.
 * Idéal pour les tests unitaires.
 */
public class InMemoryBankRepository implements BankRepository {

    // Simule le "Fichier" ou la "Base de données"
    private Bank storedBank;

    @Override
    public void save(Bank bank) {
        // En mémoire, on stocke simplement la référence
        // (Dans un cas réel complexe, on ferait une copie profonde pour éviter les effets de bord)
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