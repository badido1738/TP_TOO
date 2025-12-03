package bank;

import bank.tx.TransactionType;
import errors.InvalidAmountException;
import errors.UnknownAccountException;
import errors.TransferException;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        SavingsAccount a1 = new SavingsAccount("SA-1001", 200.0, 0.018);
        CreditAccount a2 = new CreditAccount("CA-9001", 0.0, 500.0);

        Map<String, Account> accounts = new HashMap<>();
        accounts.put(a1.getAccountNumber(), a1);
        accounts.put(a2.getAccountNumber(), a2);

        Bank bank = new Bank(accounts);

        try {
            a1.deposit(50);
            a1.withdraw(20);
            a2.withdraw(100);

            a1.applyInterest();

            try {
                System.out.println("\nTransfert réussi : 50€ de a1 → a2");
                bank.transfer(a1.getAccountNumber(), a2.getAccountNumber(), 50);
            } catch (TransferException | UnknownAccountException | InvalidAmountException e) {
                System.out.println("Erreur transfert réussi : " + e.getMessage());
            }

            // Transfert échoué
            try {
                System.out.println("\nTransfert échoué : 10 000€ de a1 → a2");
                bank.transfer(a1.getAccountNumber(), a2.getAccountNumber(), 10_000);
            } catch (TransferException | UnknownAccountException | InvalidAmountException e) {
                System.out.println("Erreur attendue (transfert impossible) : " + e.getMessage());
            }

            System.out.printf("[Savings %s] Solde: %.2f%n", a1.getAccountNumber(), a1.getBalance());
            System.out.printf("[Credit %s] Solde: %.2f%n", a2.getAccountNumber(), a2.getBalance());

            System.out.println("\nHistorique du SavingsAccount :");
            for (var t : a1.getTransactions()) {
                System.out.println(t);
            }

            System.out.println("\nHistorique du CreditAccount :");
            for (var t : a2.getTransactions()) {
                System.out.println(t);
            }

        } catch (Exception e) {
            System.out.println("Erreur inattendue : " + e.getMessage());
        }
    }
}
