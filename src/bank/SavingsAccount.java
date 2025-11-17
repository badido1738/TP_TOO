package bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import bank.tx.Transaction;
import bank.tx.TransactionType;

public final class SavingsAccount extends Account{
	
	private static LocalDateTime LocalDateTime;
	private final double interestRate;
	
	public SavingsAccount(String id, double initial, double interestRate) {
		super(id,initial);
		this.interestRate = interestRate;
	}
	
	public void applyInterest() {
		balance += balance*interestRate;
		transactions.add(new Transaction(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),TransactionType.INTEREST,0,balance));
	}
	
	@Override
	public void withdraw (double amount) {
		if(amount<0) {
			throw new BusinessRuleViolation("Amount has to be positif");
		}
		if(balance-amount<0) {
			throw new BusinessRuleViolation("Your balance is not enough");
		}
		balance = balance - amount;
		
		transactions.add(new Transaction(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),TransactionType.WITHDRAW,amount,balance));


	}
}
