package bank.tx;

import java.time.LocalDateTime;

public class Transaction {
	private final String date;
	private final TransactionType type;
	private final double montant;
	private final double soldeApres;
	

	

	public Transaction(String string, TransactionType type, double montant, double soldeApres) {
		super();
		this.date = string;
		this.type = type;
		this.montant = montant;
		this.soldeApres = soldeApres;
	}

	

	public String getDate() {
		return date;
	}



	public TransactionType getType() {
		return type;
	}



	public double getMontant() {
		return montant;
	}



	public double getSoldeApres() {
		return soldeApres;
	}



	@Override
	public String toString() {
		return "Transaction [date=" + date + ", type=" + type + ", montant=" + montant + ", soldeApres=" + soldeApres
				+ "]";
	}


}
