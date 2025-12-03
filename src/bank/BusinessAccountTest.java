package bank;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bank.tx.TransactionType;

public class BusinessAccountTest {

    private BusinessAccount account;

    @BeforeEach
    void setUp() {
        account = new BusinessAccount("B1", 200.0, 0.01, 100.0);
    }

    @Test
    void testWithdrawNominal() {
        account.withdraw(50.0);
        assertEquals(150.0, account.getBalance(), 0.001);
        assertEquals(TransactionType.WITHDRAW,
            account.transactions.get(account.transactions.size() - 1).getType());
    }

    @Test
    void testWithdrawLimit() {
        account.withdraw(300.0);
        assertEquals(-100.0, account.getBalance(), 0.001);
        assertEquals(TransactionType.WITHDRAW,
            account.transactions.get(account.transactions.size() - 1).getType());
    }

    @Test
    void testWithdrawExceedsLimit() {
        assertThrows(BusinessRuleViolation.class, () -> account.withdraw(350.0));
        assertEquals(200.0, account.getBalance(), 0.001);
    }

    @Test
    void testWithdrawNegativeAmount() {
        assertThrows(BusinessRuleViolation.class, () -> account.withdraw(-10.0));
        assertEquals(200.0, account.getBalance(), 0.001);
    }

    @Test
    void testApplyInterestPositiveBalance() {
        account.applyInterest();
        assertEquals(202.0, account.getBalance(), 0.001);
        assertEquals(TransactionType.INTEREST,
            account.transactions.get(account.transactions.size() - 1).getType());
    }

    @Test
    void testApplyInterestZeroBalance() {
        account.withdraw(200.0);
        account.applyInterest();
        assertEquals(0.0, account.getBalance(), 0.001);
        assertTrue(account.transactions.stream().noneMatch(t -> t.getType() == TransactionType.INTEREST));
    }

    @Test
    void testApplyInterestNegativeBalance() {
        account.withdraw(250.0);
        account.applyInterest();
        assertEquals(-50.0, account.getBalance(), 0.001);
        assertTrue(account.transactions.stream().noneMatch(t -> t.getType() == TransactionType.INTEREST));
    }
}
