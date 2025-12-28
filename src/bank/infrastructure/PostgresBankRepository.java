package bank.infrastructure;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import bank.domain.model.*;
import bank.domain.service.BankRepository;
import bank.domain.exceptions.PersistenceException;

public class PostgresBankRepository implements BankRepository {

    // Informations de connexion (À adapter selon ta config PgAdmin)
    private static final String URL = "jdbc:postgresql://localhost:5432/bdtoo"; // Remplace 'uppabank' par ton nom de DB
    private static final String USER = "postgres";
    private static final String PASS = "12345678"; // Mets ton mot de passe ici

    @Override
    public void save(Bank bank) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false); // Transaction SQL pour la sécurité

            String sqlAccount = "INSERT INTO accounts (id, type, balance, interest_rate, credit_limit) " +
                                "VALUES (?, ?, ?, ?, ?) " +
                                "ON CONFLICT (id) DO UPDATE SET balance = EXCLUDED.balance";
            
            String sqlTx = "INSERT INTO transactions (account_id, date, type, amount, new_balance) " +
                           "VALUES (?, ?, ?, ?, ?)"; 
                           // Note: Pour simplifier, on insère tout. Idéalement on vérifierait les doublons.

            try (PreparedStatement psAcc = conn.prepareStatement(sqlAccount);
                 PreparedStatement psTx = conn.prepareStatement(sqlTx)) {

                for (Account acc : bank.getAccounts().values()) {
                    // 1. Sauvegarde du Compte
                    psAcc.setString(1, acc.getAccountNumber());
                    psAcc.setDouble(3, acc.getBalance());

                    if (acc instanceof SavingsAccount) {
                        psAcc.setString(2, "SAVINGS");
                        psAcc.setDouble(4, ((SavingsAccount) acc).getInterestRate());
                        psAcc.setObject(5, null);
                    } else if (acc instanceof CreditAccount) {
                        psAcc.setString(2, "CREDIT");
                        psAcc.setObject(4, null);
                        psAcc.setDouble(5, ((CreditAccount) acc).getCreditLimit());
                    } else {
                         // Gérer BusinessAccount si nécessaire
                         psAcc.setString(2, "BUSINESS");
                         psAcc.setObject(4, null);
                         psAcc.setObject(5, null);
                    }
                    psAcc.executeUpdate();

                    // 2. Sauvegarde des Transactions
                 
                    PreparedStatement deleteTx = conn.prepareStatement("DELETE FROM transactions WHERE account_id = ?");
                    deleteTx.setString(1, acc.getAccountNumber());
                    deleteTx.executeUpdate();

                    for (Transaction tx : acc.getTransactions()) {
                        psTx.setString(1, acc.getAccountNumber());
                        psTx.setString(2, tx.getDate());
                        psTx.setString(3, tx.getType().toString());
                        psTx.setDouble(4, tx.getAmount()); // Assure-toi d'avoir transaction.getAmount()
                        psTx.setDouble(5, tx.getNewBalance());
                        psTx.addBatch();
                    }
                    psTx.executeBatch();
                }
            }
            conn.commit(); // Valide tout si pas d'erreur

        } catch (SQLException e) {
            throw new PersistenceException("Erreur SQL lors de la sauvegarde", e);
        }
    }

    @Override
    public Bank load() {
        Map<String, Account> accounts = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            
            // 1. Charger les comptes
            String sqlAcc = "SELECT * FROM accounts";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlAcc)) {

                while (rs.next()) {
                    String id = rs.getString("id");
                    String type = rs.getString("type");
                    double balance = rs.getDouble("balance");
                    Account acc = null;

                    if ("SAVINGS".equals(type)) {
                        double rate = rs.getDouble("interest_rate");
                        acc = new SavingsAccount(id, balance, rate);
                    } else if ("CREDIT".equals(type)) {
                        double limit = rs.getDouble("credit_limit");
                        acc = new CreditAccount(id, balance, limit);
                    }
                    // Ajouter BusinessAccount si implémenté

                    if (acc != null) {
                        accounts.put(id, acc);
                    }
                }
            }

            // 2. Charger les transactions pour chaque compte
            String sqlTx = "SELECT * FROM transactions WHERE account_id = ? ORDER BY id ASC";
            try (PreparedStatement ps = conn.prepareStatement(sqlTx)) {
                for (Account acc : accounts.values()) {
                    ps.setString(1, acc.getAccountNumber());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String date = rs.getString("date");
                            String txTypeStr = rs.getString("type");
                            // Convertir String -> Enum
                            TransactionType txType = TransactionType.valueOf(txTypeStr);
                            double amount = rs.getDouble("amount");
                            double newBal = rs.getDouble("new_balance");

                            Transaction tx = new Transaction(date, txType, amount, newBal);
                            acc.restoreTransaction(tx); // Utilise ta méthode spéciale
                        }
                    }
                }
            }

            return new Bank(accounts);

        } catch (SQLException e) {
             // Si la table n'existe pas ou DB vide, on renvoie une banque vide ou erreur selon le choix
             throw new PersistenceException("Erreur SQL lors du chargement", e);
        }
    }
}