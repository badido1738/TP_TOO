package bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import bank.tx.Transaction;
import bank.tx.TransactionType;
import errors.InvalidAmountException;
import errors.UnknownAccountException;
import errors.TransferException;

public class Bank {

    private final Map<String, Account> accounts;
    private final Logger logger = new Logger();

    public Bank(Map<String, Account> accounts) {
        this.accounts = accounts;
    }

    public void transfer(String fromId, String toId, double amount) {
        if (!accounts.containsKey(fromId)) {
            throw new UnknownAccountException("Source account not found: " + fromId);
        }
        if (!accounts.containsKey(toId)) {
            throw new UnknownAccountException("Destination account not found: " + toId);
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be > 0");
        }
        if (fromId.equals(toId)) {
            throw new InvalidAmountException("Cannot transfer to the same account");
        }

        Account from = accounts.get(fromId);
        Account to = accounts.get(toId);

        double initialFrom = from.getBalance();
        double initialTo = to.getBalance();

        try {
            from.withdraw(amount);
            to.deposit(amount);

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            from.transactions.add(new Transaction(now, TransactionType.TRANSFER_OUT, amount, from.getBalance()));
            to.transactions.add(new Transaction(now, TransactionType.TRANSFER_IN, amount, to.getBalance()));

            logger.logInfo(String.format("Transfer OK: %.2f EUR %s -> %s", amount, fromId, toId));

        } catch (Exception e) {
            from.balance = initialFrom;
            to.balance = initialTo;

            logger.logError(String.format("Transfer FAILED: %.2f EUR %s -> %s", amount, fromId, toId));

            throw new TransferException("Echec du transfert", e);
        }
    }
}
