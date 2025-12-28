package bank.persistence;

import bank.*;
import bank.tx.Transaction;
import bank.tx.TransactionType;

import java.util.HashMap;
import java.util.Map;

public class TextBankSerializer implements BankSerializer {

    private static final String DELIMITER = ";";
    private static final String NEWLINE = "\n";

    @Override
    public String serialize(Bank bank) {
        StringBuilder sb = new StringBuilder();

        // 1. Sérialisation des comptes
        // Note : Suppose l'ajout de bank.getAccounts() qui retourne la Map
        for (Account acc : bank.getAccounts().values()) {
            sb.append("ACCOUNT").append(DELIMITER);
            
            if (acc instanceof SavingsAccount) {
                SavingsAccount sa = (SavingsAccount) acc;
                sb.append("SAVINGS").append(DELIMITER)
                  .append(sa.getAccountNumber()).append(DELIMITER)
                  .append(sa.getBalance()).append(DELIMITER)
                  .append(sa.getInterestRate()); // Suppose getter existant
            } else if (acc instanceof CreditAccount) {
                CreditAccount ca = (CreditAccount) acc;
                sb.append("CREDIT").append(DELIMITER)
                  .append(ca.getAccountNumber()).append(DELIMITER)
                  .append(ca.getBalance()).append(DELIMITER)
                  .append(ca.getCreditLimit());
            } else if (acc instanceof BusinessAccount) {
                BusinessAccount ba = (BusinessAccount) acc;
                sb.append("BUSINESS").append(DELIMITER)
                  .append(ba.getAccountNumber()).append(DELIMITER)
                  .append(ba.getBalance()).append(DELIMITER)
                  .append(ba.getInterestRate()).append(DELIMITER)
                  .append(ba.getCreditLimit());
            }
            sb.append(NEWLINE);
        }

        // 2. Sérialisation des transactions
        for (Account acc : bank.getAccounts().values()) {
            for (Transaction tx : acc.getTransactions()) {
                sb.append("TX").append(DELIMITER)
                  .append(acc.getAccountNumber()).append(DELIMITER)
                  .append(tx.getDate()).append(DELIMITER) // Suppose un getter getDate() (le champ est 'date' string)
                  .append(tx.getType()).append(DELIMITER)
                  .append(tx.getAmount()).append(DELIMITER)
                  .append(tx.getNewBalance()); // Suppose un getter getNewBalance()
                sb.append(NEWLINE);
            }
        }

        return sb.toString();
    }

    @Override
    public Bank deserialize(String data) {
        Map<String, Account> accounts = new HashMap<>();
        String[] lines = data.split(NEWLINE);

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(DELIMITER);
            String recordType = parts[0];

            if ("ACCOUNT".equals(recordType)) {
                String type = parts[1];
                String id = parts[2];
                double balance = Double.parseDouble(parts[3]);

                Account acc = null;
                if ("SAVINGS".equals(type)) {
                    double rate = Double.parseDouble(parts[4]);
                    acc = new SavingsAccount(id, balance, rate);
                } else if ("CREDIT".equals(type)) {
                    double limit = Double.parseDouble(parts[4]);
                    acc = new CreditAccount(id, balance, limit);
                } else if ("BUSINESS".equals(type)) {
                    double rate = Double.parseDouble(parts[4]);
                    double limit = Double.parseDouble(parts[5]);
                    acc = new BusinessAccount(id, balance, rate, limit);
                }

                if (acc != null) {
                    accounts.put(id, acc);
                }

            } else if ("TX".equals(recordType)) {
                String accId = parts[1];
                Account acc = accounts.get(accId);
                
                if (acc != null) {
                    String date = parts[2];
                    TransactionType type = TransactionType.valueOf(parts[3]);
                    double amount = Double.parseDouble(parts[4]);
                    double newBalance = Double.parseDouble(parts[5]);

                    Transaction tx = new Transaction(date, type, amount, newBalance);
                    
                    // Note : Nécessite une méthode pour rajouter l'historique sans impact comptable
                    acc.restoreTransaction(tx); 
                }
            }
        }
        return new Bank(accounts);
    }
}