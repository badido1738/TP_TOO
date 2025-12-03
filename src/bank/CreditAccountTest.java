package bank;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bank.tx.TransactionType;

public class CreditAccountTest {

    private CreditAccount account;

    @BeforeEach
    void setUp() {
        account = new CreditAccount("C1", 0.0, 100.0);  
    }


    @Test
    void testWithdrawNominal() {
        account.withdraw(50.0);   // solde = -50
        assertEquals(-50.0, account.getBalance(), 0.001);
        assertEquals(TransactionType.WITHDRAW,
            account.transactions.getLast().getType());
    }

    @Test
    void testWithdrawLimit() {
        account.withdraw(100.0);  
        assertEquals(-100.0, account.getBalance(), 0.001);
    }

    @Test
    void testWithdrawExceedsLimit() {
        assertThrows(BusinessRuleViolation.class, () -> account.withdraw(150.0));
        assertEquals(0.0, account.getBalance(), 0.001);
    }

    @Test
    void testWithdrawNegative() {
        assertThrows(BusinessRuleViolation.class, () -> account.withdraw(-10));
        assertEquals(0.0, account.getBalance(), 0.001);
    }
}
