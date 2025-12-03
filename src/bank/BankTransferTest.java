package bank;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bank.tx.TransactionType;
import errors.InvalidAmountException;
import errors.UnknownAccountException;
import errors.TransferException;

import java.util.HashMap;
import java.util.Map;

public class BankTransferTest {

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
        double amount = 100.0;

        bank.transfer("S1", "C1", amount);

        assertEquals(100.0, source.getBalance(), 0.001);
        assertEquals(150.0, destination.getBalance(), 0.001);

        assertEquals(TransactionType.TRANSFER_OUT,
            source.getTransactions().get(source.getTransactions().size() - 1).getType());
        assertEquals(TransactionType.TRANSFER_IN,
            destination.getTransactions().get(destination.getTransactions().size() - 1).getType());
    }

    @Test
    void testTransferFailsDueToInsufficientFunds() {
        double amount = 300.0;

        assertThrows(TransferException.class,
            () -> bank.transfer("S1", "C1", amount));

        assertEquals(200.0, source.getBalance(), 0.001);
        assertEquals(50.0, destination.getBalance(), 0.001);

        assertTrue(source.getTransactions().stream().noneMatch(t -> t.getType() == TransactionType.TRANSFER_OUT));
        assertTrue(destination.getTransactions().stream().noneMatch(t -> t.getType() == TransactionType.TRANSFER_IN));
    }

    @Test
    void testTransferFailsDueToUnknownDestination() {
        double amount = 50.0;

        assertThrows(UnknownAccountException.class,
            () -> bank.transfer("S1", "UNKNOWN", amount));

        assertEquals(200.0, source.getBalance(), 0.001);
        assertTrue(source.getTransactions().stream().noneMatch(t -> t.getType() == TransactionType.TRANSFER_OUT));
    }

    @Test
    void testTransferFailsToSameAccount() {
        double amount = 50.0;

        assertThrows(InvalidAmountException.class,
            () -> bank.transfer("S1", "S1", amount));

        assertEquals(200.0, source.getBalance(), 0.001);
        assertTrue(source.getTransactions().stream().noneMatch(t -> t.getType() == TransactionType.TRANSFER_OUT));
    }
}
