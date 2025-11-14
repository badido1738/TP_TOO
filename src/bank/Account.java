package bank;

public abstract class Account {

	protected final String accountNumber;
	protected double balance;
	
	protected Account(String accountNumber, double initial) {
		this.accountNumber = accountNumber;
		this.balance = initial;
	}
	
	public final void deposit (double amount) {
		if(amount<0) {
			throw new BusinessRuleViolation("Amount has to be > 0");
		}
		balance += amount;
			
	}
	
	public abstract void withdraw (double amount);
	
	public final String getAccountNumber() {
		return accountNumber;
	}
	
	public final double getBalance() {
		return balance;
	}
	
	
}
