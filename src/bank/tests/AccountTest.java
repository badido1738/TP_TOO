package bank.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bank.domain.exceptions.BusinessRuleViolation;
import bank.domain.model.Account;
import bank.domain.model.SavingsAccount;
import bank.domain.model.TransactionType;

public class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new SavingsAccount("A1", 100.0, 0.01);
    }

//pour le deposit

    @Test
    void testDepositNominal() {
        account.deposit(50.0);
        assertEquals(150.0, account.getBalance(), 0.001);
        assertEquals(TransactionType.DEPOSIT,
            account.transactions.getLast().getType());
    }

    @Test
    void testDepositLimit() {
        account.deposit(0.01);
        assertEquals(100.01, account.getBalance(), 0.001);
    }

    @Test
    void testDepositError() {
        assertThrows(BusinessRuleViolation.class, () -> account.deposit(0));
        assertThrows(BusinessRuleViolation.class, () -> account.deposit(-5));

        assertEquals(100.0, account.getBalance(), 0.001);
    }
}
