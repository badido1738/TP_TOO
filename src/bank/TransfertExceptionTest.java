package bank;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import errors.InvalidAmountException;
import errors.UnknownAccountException;
import errors.TransferException;

import java.util.HashMap;
import java.util.Map;

public class TransfertExceptionTest {

    private SavingsAccount source;
    private CreditAccount destination;
    private Bank bank;
    private Map<String, Account> accounts;

    @BeforeEach
    void setUp() {
        source = new SavingsAccount("S1", 200.0, 0.01);
        destination = new CreditAccount("C1", 50.0, 100.0);
        accounts = new HashMap<>();
        accounts.put(source.getAccountNumber(), source);
        accounts.put(destination.getAccountNumber(), destination);
        bank = new Bank(accounts);
    }

    @Test
    void testSuccessfulTransfer() {
        bank.transfer("S1", "C1", 100.0);
        assertEquals(100.0, source.getBalance(), 0.001);
        assertEquals(150.0, destination.getBalance(), 0.001);
    }

    @Test
    void testInvalidAmount() {
        assertThrows(InvalidAmountException.class, () -> bank.transfer("S1", "C1", -50.0));
    }

    @Test
    void testUnknownAccount() {
        assertThrows(UnknownAccountException.class, () -> bank.transfer("S1", "UNKNOWN", 50.0));
    }

    @Test
    void testTransferExceptionRollback() {
        CreditAccount faulty = new CreditAccount("C2", 0.0, 0.0) {
            @Override
            public void deposit(double amount) {
                throw new RuntimeException("Deposit failed");
            }
        };
        accounts.put(faulty.getAccountNumber(), faulty);

        assertThrows(TransferException.class, () -> bank.transfer("S1", "C2", 50.0));

        assertEquals(200.0, source.getBalance(), 0.001);
        assertEquals(0.0, faulty.getBalance(), 0.001);
    }
}
