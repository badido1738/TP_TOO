package bank;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bank.tx.TransactionType;

public class BankTransferTest {

    private SavingsAccount source;
    private CreditAccount destination;
    private Bank bank;

    @BeforeEach
    void setUp() {
        source = new SavingsAccount("S1", 200.0, 0.01);
        destination = new CreditAccount("C1", 50.0, 100.0);
        bank = new Bank();
    }

//transfert success
    @Test
    void testSuccessfulTransfer() {
        double amount = 100.0;

        bank.transfer(source, destination, amount);

        assertEquals(100.0, source.getBalance(), 0.001);
        assertEquals(150.0, destination.getBalance(), 0.001);

        assertEquals(TransactionType.TRANSFER_OUT,
            source.transactions.get(source.transactions.size() - 1).getType());
        assertEquals(TransactionType.TRANSFER_IN,
            destination.transactions.get(destination.transactions.size() - 1).getType());
    }

//atomicite
    @Test
    void testTransferFailsDueToInsufficientFunds() {
        double amount = 300.0; 

        assertThrows(BusinessRuleViolation.class,
            () -> bank.transfer(source, destination, amount));

        assertEquals(200.0, source.getBalance(), 0.001);
        assertEquals(50.0, destination.getBalance(), 0.001);

        assertTrue(source.transactions.stream().noneMatch(t -> t.getType() == TransactionType.TRANSFER_OUT));
        assertTrue(destination.transactions.stream().noneMatch(t -> t.getType() == TransactionType.TRANSFER_IN));
    }

    @Test
    void testTransferFailsDueToNullDestination() {
        double amount = 50.0;

        assertThrows(BusinessRuleViolation.class,
            () -> bank.transfer(source, null, amount));

        assertEquals(200.0, source.getBalance(), 0.001);

        assertTrue(source.transactions.stream().noneMatch(t -> t.getType() == TransactionType.TRANSFER_OUT));
    }

    @Test
    void testTransferFailsToSameAccount() {
        double amount = 50.0;

        assertThrows(BusinessRuleViolation.class,
            () -> bank.transfer(source, source, amount));

        assertEquals(200.0, source.getBalance(), 0.001);

        assertTrue(source.transactions.stream().noneMatch(t -> t.getType() == TransactionType.TRANSFER_OUT));
    }
}
