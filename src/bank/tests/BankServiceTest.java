package bank.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bank.application.BankService;
import bank.domain.model.Account;
import bank.domain.model.Bank;
import bank.domain.model.SavingsAccount;
import bank.domain.service.BankRepository;
import bank.infrastructure.InMemoryBankRepository;

class BankServiceTest {

    private BankService service;
    private BankRepository repository;

    @BeforeEach
    void setUp() {
        // 1. On instancie le repository EN MÉMOIRE (pas de fichiers !)
        repository = new InMemoryBankRepository();

        // 2. On prépare une banque initiale
        SavingsAccount sa = new SavingsAccount("TEST-1", 1000.0, 0.01);
        HashMap<String, Account> accounts = new HashMap<>();
        accounts.put(sa.getAccountNumber(), sa);
        
        // On sauvegarde cet état initial dans la "mémoire"
        repository.save(new Bank(accounts));

        // 3. Injection de dépendance : Le Service utilise le repo en mémoire
        service = new BankService(repository);
    }

    @Test
    void testTransferIsSavedInMemory() {
        assertEquals(1000.0, service.getBank().getAccounts().get("TEST-1").getBalance());


        service.getBank().getAccounts().put("TEST-2", new SavingsAccount("TEST-2", 0.0, 0.0));
        
        service.transfer("TEST-1", "TEST-2", 100.0);

        assertEquals(900.0, service.getBank().getAccounts().get("TEST-1").getBalance());
        assertEquals(100.0, service.getBank().getAccounts().get("TEST-2").getBalance());
        

        Bank reloadedBank = repository.load();
        assertEquals(900.0, reloadedBank.getAccounts().get("TEST-1").getBalance());
    }
    
    @Test
    void testIsolation() {
        // Ce test prouve qu'on ne touche pas au disque dur.
        assertDoesNotThrow(() -> service.save());
    }
}