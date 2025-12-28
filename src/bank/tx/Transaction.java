package bank.tx;

public class Transaction {
    private final String date;
    private final TransactionType type;
    private final double amount; // Le champ 'amount' doit exister
    private final double newBalance;

    public Transaction(String date, TransactionType type, double amount, double newBalance) {
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.newBalance = newBalance;
    }

    public TransactionType getType() {
        return type;
    }

    /** Méthode manquante ou mal nommée */
    public double getAmount() { // <--- AJOUTEZ OU VÉRIFIEZ CELA
        return amount;
    }

    // Le reste des méthodes...
    @Override
    public String toString() {
        return String.format("[%s] %s: %.2f EUR (Solde: %.2f)", date, type, amount, newBalance);
    }
    
    public String getDate() {
        return date;
    }

    public double getNewBalance() {
        return newBalance;
    }
}