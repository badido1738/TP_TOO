package bank;

public class BusinessRuleViolation extends RuntimeException {
	public BusinessRuleViolation (String message) { super(message) ;}
	// recommended messages :
	// " amount must be > 0" , " insufficient funds " , " over credit
}
