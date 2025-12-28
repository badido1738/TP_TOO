package bank.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bank.application.BankService;
import bank.domain.exceptions.TransferException;
import bank.domain.model.Account;
import bank.domain.model.Bank;
import bank.domain.model.CreditAccount;
import bank.domain.model.SavingsAccount;
import bank.domain.service.BankRepository;
import bank.domain.service.FixedFeePolicy;
import bank.infrastructure.FileBankRepository;
import bank.infrastructure.InMemoryBankRepository;
import bank.infrastructure.TextBankSerializer;

class BankSystemTest {

    private BankService service;
    private SavingsAccount source;
    private CreditAccount dest;

    @BeforeEach
    void setUp() {
        // SETUP MÉTIER : On utilise le InMemoryRepository pour la rapidité
        BankRepository memoryRepo = new InMemoryBankRepository();
        
        source = new SavingsAccount("SRC", 1000.0, 0.01);
        dest = new CreditAccount("DST", 0.0, 500.0);
        
        Map<String, Account> accounts = new HashMap<>();
        accounts.put(source.getAccountNumber(), source);
        accounts.put(dest.getAccountNumber(), dest);
        
        Bank bank = new Bank(accounts);
        memoryRepo.save(bank); // On initialise le repo
        
        service = new BankService(memoryRepo);
    }

    // --- CAS 1 : Transfert valide ---
    @Test
    void testTransferSuccess() {
        service.transfer("SRC", "DST", 100.0);

        assertEquals(900.0, source.getBalance(), 0.001, "Le compte source doit être débité");
        assertEquals(100.0, dest.getBalance(), 0.001, "Le compte destination doit être crédité");
    }

    // --- CAS 2 : Atomicité 
    @Test
    void testTransferAtomicityOnFailure() {

        assertThrows(TransferException.class, () -> {
            service.transfer("SRC", "DST", 2000.0);
        });

        // VÉRIFICATION DE L'ATOMICITÉ : Les soldes ne doivent pas avoir bougé
        assertEquals(1000.0, source.getBalance(), 0.001, "Le solde source doit être intact après échec");
        assertEquals(0.0, dest.getBalance(), 0.001, "Le solde destination doit être intact après échec");
    }

    // --- CAS 3 : Politique de Frais (Strategy) ---
    @Test
    void testFeePolicyApplication() {
        // On applique une politique de frais fixes de 5€ au compte source
        source.setFeePolicy(new FixedFeePolicy(5.0));
        
        source.withdraw(100.0);

        // Solde attendu : 1000 - 100 (retrait) - 5 (frais) = 895
        assertEquals(895.0, source.getBalance(), 0.001);
        
        // Vérification de l'historique : Retrait + Frais
        assertEquals(2, source.getTransactions().size(), "On attend 2 transactions (retrait + frais)");
    }

    // --- CAS 4 : Persistance (Fichiers) ---
    @Test
    void testPersistenceAndReload() throws IOException {
        // 1. Préparation : Fichier temporaire pour ne pas polluer le projet
        Path tempFile = Files.createTempFile("bank_test", ".txt");
        
        try {
            // 2. Sauvegarde
            BankRepository fileRepo = new FileBankRepository(tempFile, new TextBankSerializer());
            
            // On reprend la banque de l'état initial (avec SRC=1000, DST=0)
            fileRepo.save(service.getBank());

            // 3. Rechargement dans une NOUVELLE instance
            Bank loadedBank = fileRepo.load();
            Account loadedSource = loadedBank.getAccounts().get("SRC");

            // 4. Vérifications
            assertNotNull(loadedSource, "Le compte sauvegardé doit être retrouvé");
            assertEquals(1000.0, loadedSource.getBalance(), 0.001, "Le solde doit être conservé");
            assertEquals("SRC", loadedSource.getAccountNumber());
            
        } finally {
            // Nettoyage du fichier de test
            Files.deleteIfExists(tempFile);
        }
    }
}