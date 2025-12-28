package bank.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bank.domain.exceptions.BusinessRuleViolation;
import bank.domain.model.SavingsAccount;
import bank.domain.model.TransactionType;

public class SavingsAccountTest {

    private SavingsAccount account;

    @BeforeEach
    void setUp() {
        account = new SavingsAccount("S1", 100.0, 0.01);
    }
//pour le withdraw
    @Test
    void testWithdrawNominal() {
        account.withdraw(40.0);
        assertEquals(60.0, account.getBalance(), 0.001);
        assertEquals(TransactionType.WITHDRAW,
            account.transactions.getLast().getType());
    }

    @Test
    void testWithdrawLimitToZero() {
        account.withdraw(100.0);
        assertEquals(0.0, account.getBalance(), 0.001);
        assertEquals(TransactionType.WITHDRAW,
            account.transactions.getLast().getType());
    }

    @Test
    void testWithdrawTooMuch() {
        assertThrows(BusinessRuleViolation.class, () -> account.withdraw(150.0));

        assertEquals(100.0, account.getBalance(), 0.001);
    }

    @Test
    void testWithdrawNegativeAmount() {
        assertThrows(BusinessRuleViolation.class, () -> account.withdraw(-10.0));
        assertEquals(100.0, account.getBalance(), 0.001);
    }
}
