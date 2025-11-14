package bank;

public class CreditAccount extends Account {
	private final double creditLimit;
	
	public CreditAccount(String id, double initial, double creditLimit) {
		super(id,initial);
		this.creditLimit = creditLimit;
	}
	
	@Override
	public void withdraw (double amount) {
		if (amount<0) {
			throw new BusinessRuleViolation("Amount has to be > 0");
		}
		if(balance-amount>creditLimit) {
			throw new BusinessRuleViolation("You violated the limit of your credit");
		}
		
		balance -= amount;
	}

}
